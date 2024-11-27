package com.iamdvh.UserService.exception;

import com.iamdvh.UserService.dto.response.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobleException {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse> handleAppException(AppException e){
        TypeCode typeCode = e.getTypeCode();

        return ResponseEntity.badRequest().body(APIResponse.builder()
                        .code(typeCode.getCode())
                        .message(typeCode.getMessage())
                .build());
    }    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMANVE(MethodArgumentNotValidException e){
        TypeCode typeCode = TypeCode.valueOf(e.getFieldError().getDefaultMessage());
        return ResponseEntity.badRequest().body(APIResponse.builder()
                        .code(typeCode.getCode())
                        .message(typeCode.getMessage())
                .build());
    }
}
