package com.example.groupboardservice.data.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    private String result;
    private String message;

    public ResponseDto() {
        result = "000";
        message = "Success";
    }
}
