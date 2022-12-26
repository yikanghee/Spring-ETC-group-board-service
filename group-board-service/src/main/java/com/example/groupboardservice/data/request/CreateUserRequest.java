package com.example.groupboardservice.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String id;
    private String email;
    private String password;
}
