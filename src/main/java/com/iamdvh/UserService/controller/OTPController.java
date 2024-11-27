package com.iamdvh.UserService.controller;

import com.iamdvh.UserService.dto.request.OTPRequest;
import com.iamdvh.UserService.dto.response.APIResponse;
import com.iamdvh.UserService.dto.response.OTPResponse;
import com.iamdvh.UserService.service.OTPService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OTPController {
    OTPService otpService;
    @PostMapping(value = "")
    public APIResponse<OTPResponse> sentSms(@RequestBody OTPRequest request) throws JOSEException {
        return APIResponse.<OTPResponse>builder()
                .data(otpService.callStringeeAPI(request.getPhoneNumber()))
                .build();
    }
    @PostMapping(value = "/confirm")
    public APIResponse<OTPResponse> confirmOtp(@RequestBody OTPRequest request) throws JOSEException {
        return APIResponse.<OTPResponse>builder()
                .data(otpService.confirm(request.getPhoneNumber(), request.getOtp()))
                .build();
    }

}
