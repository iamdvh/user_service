package com.iamdvh.UserService.service;
import com.iamdvh.UserService.dto.request.IntrospectRequest;
import com.iamdvh.UserService.dto.request.LoginRequest;
import com.iamdvh.UserService.dto.response.IntrospectResponse;
import com.iamdvh.UserService.dto.response.LoginResponse;
import com.iamdvh.UserService.entity.UserEntity;
import com.iamdvh.UserService.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;
    UserRepository userRepository;
    public LoginResponse login(LoginRequest request) throws JOSEException {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var result = passwordEncoder.matches(request.getPassword(), user.getPassword());
        return  LoginResponse.builder()
                .authenticate(result)
                .token(generateToken(user))
                .build();
    }

    private String generateToken(UserEntity user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("iamdvh.com")
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(Duration.ofHours(1)).toEpochMilli()
                ))
                .build();
        JWSObject jwsObject = new JWSObject(header, claimsSet.toPayload());
        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        return jwsObject.serialize();
    }
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(request.getToken());
        var result = signedJWT.verify(jwsVerifier);
        var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return IntrospectResponse.builder()
                .authenticate(result && expirationTime.after(new Date()))
                .build();
        }

}
