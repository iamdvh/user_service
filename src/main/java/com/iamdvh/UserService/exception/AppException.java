package com.iamdvh.UserService.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class AppException extends RuntimeException {
    public AppException(TypeCode typeCode){
        this.typeCode = typeCode;
    }
    private TypeCode typeCode;

    public TypeCode getTypeCode() {
        return typeCode;
    }
}
