package com.example.groupboardservice.filter;

import com.example.groupboardservice.config.auth.JwtStatus;
import com.example.groupboardservice.config.auth.JwtTokenProvider;
import com.example.groupboardservice.config.auth.JwtTokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.token.access.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.token.access.name}")
    private String accessTokenName;

    private final JwtTokenProvider jwtTokenProvider;

    private final List<String> url = Collections.unmodifiableList(
        Arrays.asList( // JWT 필터에서 실행을 제외할 URL 요청 정의
            "/signup",
            "/login"
        )
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        log.info(this.getClass().getName() + ".doFilterInternal start");

        // 쿠키에서 토큰 가져오기
        String accessToken = jwtTokenProvider.resolveToken(request, JwtTokenType.ACCESS_TOKEN);
        log.info("accessToken : {}", accessToken);

        // Access Token 유효기간 검증
        JwtStatus accessTokenStatus = jwtTokenProvider.validateToken(accessToken);
        log.info("accessTokenStatus : {}", accessTokenStatus);

        // 유효 기간 검증
        if (accessTokenStatus == JwtStatus.ACCESS) {
            //토큰이 유효하면 토큰으로부터 유저 정보를 받아온다.
            // 받은 유저 정보의 아이디 권한을 Spring Security에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

            // SecurityContext에 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (accessTokenStatus == JwtStatus.EXPIRED ||
            accessTokenStatus == JwtStatus.DENIED) { // 만료 및 쿠키에서 삭제된 AccessToken인 경우

            // Access Token이 만료되면 Refresh Token 유효한지 체크한다.
            // Refresh Token 확인하기
            String refreshToken = jwtTokenProvider.resolveToken(request, JwtTokenType.REFRESH_TOKEN);

            // Refresh Token 유효기간 검증
            JwtStatus refreshTokenStatus = jwtTokenProvider.validateToken(refreshToken);
            log.info("refreshTokenStatus : {}", refreshTokenStatus);

            // Refresh Token이 유효하면 Access Token 재발급
            if (refreshTokenStatus == JwtStatus.ACCESS) {
                String userId = jwtTokenProvider.getUserId(refreshToken);
                String userRoles = jwtTokenProvider.getUserRoles(refreshToken);

                log.info("userId : {}", userId);
                log.info("userRoles : {}", userRoles);

                // Access Token 재발급
                String reAccessToken = jwtTokenProvider.createToken(userId, userRoles, JwtTokenType.ACCESS_TOKEN);

                log.info("accessTokenName : {}", accessTokenName);
                // 사용자 웹브라우저 쿠키에 Access Token 정보가 존재하면 삭제
                ResponseCookie cookie = ResponseCookie.from(accessTokenName, "")
                    .maxAge(0)
                    .build();

                // 만약, 기존에 존재하는 Access Token이 있다면 삭제
                response.setHeader("Set-Cookie", cookie.toString());

                cookie = null;

                cookie = ResponseCookie.from(accessTokenName, reAccessToken)
                    .domain("localhost")
                    .path("/")
                    .secure(true)
                    .sameSite("None")
                    .maxAge(accessTokenValidTime) // 만료 시간 설정
                    .httpOnly(true)
                    .build();

                response.setHeader("Set-Cookie", cookie.toString());

                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아온다.
                Authentication authentication = jwtTokenProvider.getAuthentication(reAccessToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (refreshTokenStatus == JwtStatus.EXPIRED) { // 별도 로직 처리 x -> 로그인 페이지로 강제 이동
                log.info("Refresh Token 만료");
            } else { // 별도 로직 처리 x -> 로그인 페이지로 강제 이동
                log.info("Refresh Token 오류");
            }

        }

        log.info(this.getClass().getName() + ".doFilterInternal end");
        filterChain.doFilter(request, response);
    }

    /**
     * JwtAuthenticationFilter가 체크하지 않을 URL 체크하여 호출 안 하기 위에 url 정의
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return url.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }
}
