package com.example.groupboardservice.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class CustomExceptionEntity {

    private final String result;
    private final String message;

    @Builder
    public CustomExceptionEntity(HttpStatus status, String result, String message) {
        this.result = result;
        this.message = message;
    }
}
