package com.example.groupboardservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    // 비밀번호 암호화 할 때 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 로그인 인증 결과 저장용 객체
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable(); // post 전송을 위해 csrf 막기
        http
                .formLogin(login -> login // 로그인 페이지 생성
                        .loginPage("/jwt/loginForm")
                        .loginProcessingUrl("/jwt/loginProc")
                        .usernameParameter("userId") // 로그인 ID로 사용할 html input 객체의 name 값
                        .passwordParameter("userPassword") // 로그인 패스워드로 사용할 html의 input 객체의 name 값

                        // 로그인 처리
                        .successForwardUrl("/jwt/loginSuccess") // 로그인 성공
                        .failureUrl("/jwt/loginFail") // 로그인 실패
                )

                // 로그아웃 처리
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )

                // 세션을 사용하지 않도록 설정
                .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeRequests() // 페이지 접속 권한 설정
                .antMatchers("/info/**").hasAnyAuthority("ROLE_USER") // 유저 권한
                .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN") // 관리자 권한
                .anyRequest().permitAll() // 이 외 나머지 url은 인증을 받지 않아도 접속가능
                .and()
                .httpBasic();

        return http.build();
    }


}

