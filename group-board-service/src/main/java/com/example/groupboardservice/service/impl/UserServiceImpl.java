package com.example.groupboardservice.service.impl;

import com.example.groupboardservice.config.auth.JwtTokenProvider;
import com.example.groupboardservice.config.auth.JwtTokenType;
import com.example.groupboardservice.config.auth.RoleType;
import com.example.groupboardservice.data.domain.users.User;
import com.example.groupboardservice.data.request.CreateUserRequest;
import com.example.groupboardservice.data.request.LoginUserRequest;
import com.example.groupboardservice.exception.CustomException;
import com.example.groupboardservice.exception.enums.ExceptionEnum;
import com.example.groupboardservice.repository.UserRepository;
import com.example.groupboardservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void createUser(CreateUserRequest user) {
        log.info(this.getClass().getName() + ".createUser start");

        // 유저 이메일이 존재하나 먼저 체크
        if (userRepository.findByEmail(user.getEmail()) != null) {
            // 사용중인 이메일입니다.
            throw new CustomException(ExceptionEnum.APP_EMAIL_ALREADY);
        }

        // 유저 아이디 중복 체크
        if (userRepository.findById(user.getId()) != null) {
            // 사용중인 아이디입니다.
            throw new CustomException(ExceptionEnum.APP_ID_ALREADY);
        }

        log.info(this.getClass().getName() + ".createUser end");
        // 저장
        userRepository.save(User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(RoleType.USER.getRole())
                .build());
    }

    @Override
    public String loginUser(LoginUserRequest user) {
        log.info(this.getClass().getName() + "loginUser start");

        User baseUser = userRepository.findById(user.getId());

        // 아이디가 데이터베이스에 있지 않다면 예외
        if (baseUser == null) {
            throw new CustomException(ExceptionEnum.APP_USER_NOT_FOUND);
        }

        // 아이디로 찾아온 비밀번호와 사용자가 입력한 비밀번호가 같지 않으면 예외
        if (!passwordEncoder.matches(user.getPassword(), baseUser.getPassword())) {
            throw new CustomException(ExceptionEnum.APP_INCORRECT_PASSWORD);
        }

        log.info(this.getClass().getName() + "loginUser end");
        return jwtTokenProvider.createToken(baseUser.getId(), baseUser.getRoles(), JwtTokenType.ACCESS_TOKEN);
    }
}
