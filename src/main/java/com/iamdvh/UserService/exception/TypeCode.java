package com.iamdvh.UserService.exception;

public enum TypeCode {
    USER_NOT_FOUND(1001, "User not found."),
    INVALID_USERNAME(1002, "Username must be least 8 character.")
    ;
    private int code;
    private String message;

    TypeCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
