package com.example.groupboardservice.controller;

import com.example.groupboardservice.exception.CustomException;

import static com.example.groupboardservice.exception.enums.ExceptionEnum.APP_USER_NOT_FOUND;

@org.springframework.stereotype.Service
public class Service {

    public String getName(String name) {

        if (!name.isBlank()) {
            throw new CustomException(APP_USER_NOT_FOUND);
        }
        return name;
    }
}
