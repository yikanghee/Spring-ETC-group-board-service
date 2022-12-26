package com.example.groupboardservice.exception.enums;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ExceptionEnum {

    APP_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "101", "User not found"),
    APP_EMAIL_ALREADY(HttpStatus.BAD_REQUEST, "102", "This Email is Already in Use"),
    APP_ID_ALREADY(HttpStatus.BAD_REQUEST, "103", "This Id is Already in Use"),
    APP_INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "104", "Incorrect password"),

    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "801", "Access Denied"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "802", "Bad Request"),
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "803", "Runtime exception"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "999", "Internal server error")
    ;
    private final HttpStatus status;
    private final String result;
    private final String message;

    ExceptionEnum(HttpStatus status, String result, String message) {
        this.status = status;
        this.result = result;
        this.message = message;
    }
}
