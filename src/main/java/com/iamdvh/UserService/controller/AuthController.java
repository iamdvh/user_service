package com.iamdvh.UserService.controller;

import com.iamdvh.UserService.dto.request.IntrospectRequest;
import com.iamdvh.UserService.dto.request.LoginRequest;
import com.iamdvh.UserService.dto.response.APIResponse;
import com.iamdvh.UserService.dto.response.IntrospectResponse;
import com.iamdvh.UserService.dto.response.LoginResponse;
import com.iamdvh.UserService.service.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public APIResponse<LoginResponse> login(@RequestBody LoginRequest request) throws JOSEException {
        return APIResponse.<LoginResponse>builder()
                .data(authService.login(request))
                .build();
    }
    @PostMapping("/introspect")
    public APIResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        return APIResponse.<IntrospectResponse>builder()
                .data(authService.introspect(request))
                .build();
    }
}
