package com.example.groupboardservice.data.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomResponseDto<T> extends ResponseDto{
    private T data;

    public CustomResponseDto(T data) {
        this.data = data;
    }
}
