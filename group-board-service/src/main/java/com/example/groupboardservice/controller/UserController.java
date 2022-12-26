package com.example.groupboardservice.controller;

import com.example.groupboardservice.data.request.CreateUserRequest;
import com.example.groupboardservice.data.request.LoginUserRequest;
import com.example.groupboardservice.data.response.CustomResponseDto;
import com.example.groupboardservice.data.response.ResponseDto;
import com.example.groupboardservice.data.response.TokenResponse;
import com.example.groupboardservice.exception.CustomException;
import com.example.groupboardservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.example.groupboardservice.exception.enums.ExceptionEnum.APP_USER_NOT_FOUND;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String testApi(@RequestParam String name) {
        if (name.isBlank()) {
            throw new CustomException(APP_USER_NOT_FOUND);
        }
        return name;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseDto createUser(@RequestBody CreateUserRequest user) {
        log.info(this.getClass().getName() + ".createUser start");

        userService.createUser(user);

        log.info(this.getClass().getName() + ".createUser end");
        return new ResponseDto();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginUserRequest user) {
        log.info(this.getClass().getName() + ".login start");

        // 여기서 리턴 값으로 엑세스 토큰을 받아서 사용해야 한다.
        String accessToken = userService.loginUser(user);

        log.info(this.getClass().getName() + ".login end");
        return new CustomResponseDto<>(TokenResponse.accessTokenResponse(accessToken));
    }
}
