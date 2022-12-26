package com.example.groupboardservice.config.auth;

import com.example.groupboardservice.data.dto.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@RequiredArgsConstructor
public class AuthInfo implements UserDetails {

    private final UserDto userDto;

    // 로그인한 사용자의 권한 부여하기
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> requestSet = new HashSet<>();

        String roles = userDto.getRoles();

        if (roles.length() > 0) { // DB에 저장된 Role이 있는 경우에만 실행
            for (String role : roles.split(",")) {
                requestSet.add(new SimpleGrantedAuthority(role));
            }
        }

        return requestSet;
    }

    // 사용자의 password 반환
    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    // 사용자의 ID 반환(unique한 값)
    @Override
    public String getUsername() {
        return userDto.getId();
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // true -> 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; // true -> 잠금되지 않았음
    }

    // 패스워드의 만료 여부반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true -> 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true; // true -> 사용 가능
    }
}
