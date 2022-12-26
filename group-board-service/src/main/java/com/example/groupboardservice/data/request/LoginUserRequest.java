package com.example.groupboardservice.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRequest {
    private String id;
    private String password;
}
