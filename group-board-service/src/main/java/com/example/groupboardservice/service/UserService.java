package com.example.groupboardservice.service;

import com.example.groupboardservice.data.request.CreateUserRequest;
import com.example.groupboardservice.data.request.LoginUserRequest;

public interface UserService {

    // 회원 가입
    void createUser(CreateUserRequest user);

    String loginUser(LoginUserRequest user);
}
