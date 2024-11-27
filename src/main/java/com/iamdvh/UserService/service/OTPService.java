package com.iamdvh.UserService.service;

import com.iamdvh.UserService.dto.response.OTPResponse;
import com.iamdvh.UserService.entity.UserEntity;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OTPService {
    Timer timer = new Timer();
    RestTemplate restTemplate;
    private final Map<String, String> otpStorage = new HashMap<>();
    public OTPResponse confirm(String phoneNumber, String otp){
        var storeOtp = otpStorage.get(phoneNumber);
        var result =  storeOtp != null && storeOtp.equals(otp);
        return OTPResponse.builder()
                .isCorrect(result)
                .build();
    }

    public OTPResponse callStringeeAPI(String phoneNumber) throws JOSEException {
        var otp = generateOTP();
        storeOTP(phoneNumber, otp);
        // Cấu hình headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + generateToken());
        headers.set("Cookie", "SRVNAME=SD");

        // Dữ liệu JSON
        Map<String, Object> data = new HashMap<>();
        Map<String, String> from = new HashMap<>();
        from.put("type", "external");
        from.put("number", "842871061791");
        from.put("alias", "STRINGEE_NUMBER");

        Map<String, String> toNumber = new HashMap<>();
        toNumber.put("type", "external");
        toNumber.put("number", phoneNumber);
        toNumber.put("alias", "TO_NUMBER");

        Map<String, String> action = new HashMap<>();
        action.put("action", "talk");
        action.put("text", otp + ",,,,," + otp);

        data.put("from", from);
        data.put("to", new Map[]{toNumber});
        data.put("answer_url", "https://example.com/answerurl");
        data.put("actions", new Map[]{action});

        // Tạo request
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);

        // Gọi API Stringee
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.stringee.com/v1/call2/callout",
                HttpMethod.POST,
                request,
                String.class
        );

        // Trả về response từ API Stringee
        return OTPResponse.builder().body(response.getBody()).build();
    }
    private String generateToken() throws JOSEException {
        final String SECRET_KEY = "WW1lVG4zRkVsQnpUZWhleHFsTDlRV2lSRVd3eFY0THU=";
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .contentType("stringee-api;v=1")
                .build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .issuer("SK.0.4dAp5ubfK9IOdWd291XggEZOEqJCDPbB")
                .expirationTime(new Date(
                        Instant.now().plus(Duration.ofHours(1)).toEpochMilli()
                ))
                .claim("rest_api", true)
                .build();
        JWSObject jwsObject = new JWSObject(header, claimsSet.toPayload());
        jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
        return jwsObject.serialize();
    }
    private void storeOTP(String phoneNumber, String otp){
        otpStorage.put(phoneNumber, otp);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                otpStorage.remove(phoneNumber);
            }
        } , 5 *60*1000);
    }
    public String generateOTP(){
          final String OTP_CHARACTERS = "0123456789"; // Chỉ sử dụng số
          final SecureRandom RANDOM = new SecureRandom();
        StringBuilder otp = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(OTP_CHARACTERS.length());
            otp.append(OTP_CHARACTERS.charAt(index));
        }
        return otp.toString();
    }
}
