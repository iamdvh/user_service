package com.iamdvh.UserService.controller;

import com.iamdvh.UserService.dto.request.UserRequest;
import com.iamdvh.UserService.dto.response.APIResponse;
import com.iamdvh.UserService.dto.response.PageResponse;
import com.iamdvh.UserService.dto.response.UserResponse;
import com.iamdvh.UserService.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    @PostMapping("/create")
    APIResponse<UserResponse> create(@RequestBody @Valid UserRequest request) throws JOSEException {
        return APIResponse.<UserResponse>builder()
                .data(userService.create(request))
                .build();
    }
    @PutMapping("/{id}")
    APIResponse<UserResponse> update(@PathVariable("id") String id, @RequestBody UserRequest request){
        return APIResponse.<UserResponse>builder()
                .data(userService.update(id, request))
                .build();
    }
    @GetMapping("/{id}")
    APIResponse<UserResponse> findUser(@PathVariable("id") String id){
        return APIResponse.<UserResponse>builder()
                .data(userService.findByIdVer2(id))
                .build();
    }
    @DeleteMapping("/{id}")
    APIResponse<UserResponse> delete(@PathVariable("id") String id){
        userService.delete(id);
        return APIResponse.<UserResponse>builder()
                .message("Delete successfully.")
                .build();
    }
    @GetMapping("")
    APIResponse<PageResponse<UserResponse>> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size)
    {
        return APIResponse.<PageResponse<UserResponse>>builder()
                .data(userService.findAll(page,size))
                .build();
    }
}
