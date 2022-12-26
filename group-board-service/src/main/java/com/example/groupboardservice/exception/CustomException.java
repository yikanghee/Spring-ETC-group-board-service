package com.example.groupboardservice.exception;

import com.example.groupboardservice.exception.enums.ExceptionEnum;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionEnum exception;

    public CustomException(ExceptionEnum ex) {
        super(ex.getMessage());
        this.exception = ex;
    }

}
