package com.example.groupboardservice.config.auth;

import com.example.groupboardservice.data.dto.UserDto;
import com.example.groupboardservice.service.UserService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.creator}")
    private String creator;

    @Value("${jwt.token.access.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.token.access.name}")
    private String accessTokenName;

    @Value("${jwt.token.refresh.valid.time}")
    private long refreshTokenValidTime;

    @Value("${jwt.token.refresh.name}")
    private String refreshTokenName;

    /**
     * JWT 토큰 (Access Token, Refresh Token) 생성
     * @param userId 회원 아이디
     * @param roles 회원 권한
     * @param tokenType 토큰 유형
     * @return 인증 처리한 정보(성공, 실패)
     */
    public String createToken(String userId, String roles, JwtTokenType tokenType) {
        log.info(this.getClass().getName() + ".createToken start !");
        log.info("userId : {}", userId);

        long validTime = 0;

        // 토큰 별 유효 시간을 다르게 설정
        if (tokenType == JwtTokenType.ACCESS_TOKEN) {
            validTime = (accessTokenValidTime);
        } else if (tokenType == JwtTokenType.REFRESH_TOKEN) {
            validTime = (refreshTokenValidTime);
        }

        Claims claims = Jwts.claims()
                .setIssuer(creator) // JWT 토큰 생성자
                .setSubject(userId); // 회원 아이디 (PK) 저장

        claims.put("roles", roles); // JWT Payload에 정의된 기본 옵션 외 정보를 추가 : 사용자 권한 추가
        Date now = new Date();

        log.info(this.getClass().getName() + ".createToken end !");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + (validTime * 1000)))
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 키
                .compact();
    }

    public Authentication getAuthentication(String token) {
        log.info(this.getClass().getName() + ".getAuthentication start");
        log.info("getAuthentication : {}", token);

        // JWT 토큰에 저장된 사용자 아이디
        String userId = getUserId(token);
        log.info("userId : {}", userId);

        // todo 유저 인포 디티오에 정보 받아온다 비밀번호 검증
        UserDto userDto = new UserDto();

        // DB에 저장된 사용자 권한 가져오기
        String roles = userDto.getRoles();

        Set<GrantedAuthority> requestSet = new HashSet<>();
        if (roles.length() > 0) { // DB에 Role이 있는 경우에만 실행
            for (String role : roles.split(",")) {
                requestSet.add(new SimpleGrantedAuthority(role));
            }
        }

        log.info(this.getClass().getName() + ".getAuthentication end");
        // Spring Security가 로그인 성공된 정보를 Spring Security에서 사용하기 위해
        // Spring Security용 UsernamePasswordAuthenticationToken 생성
        return new UsernamePasswordAuthenticationToken(userDto, "", requestSet);
    }


    /**
     * JWT 토큰(Access Token, Refresh Token)에서 회원 정보 추출
     * @param token 토큰
     * @return 회원 아이디
     */
    public String getUserId(String token) {
        log.info(this.getClass().getName() + ".getUserId start");

        String userId = Jwts.parser().setSigningKey(secretKey)
            .parseClaimsJws(token).getBody().getSubject();
        log.info("userId : {}", userId);

        log.info(this.getClass().getName() + ".getUserId end");
        return userId;
    }

    public String getUserRoles(String token) {
        log.info(this.getClass().getName() + ".getUserRoles start");

        String roles = (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            .getBody().get("roles");
        log.info("roles : {}", roles);

        log.info(this.getClass().getName() + ".getUserRoles end");
        return roles;
    }


    /**
     * 쿠키에 저장된 JWT 토큰 가져오기
     * @param request 정보
     * @return 쿠키에 저장된 토큰 값
     */
    public String resolveToken(HttpServletRequest request, JwtTokenType tokenType) {
        log.info(this.getClass().getName() + ".resolveToken start");

        String tokenName = "";

        if (tokenType == JwtTokenType.ACCESS_TOKEN) {
            tokenName = accessTokenName;
        } else if (tokenType == JwtTokenType.REFRESH_TOKEN) {
            tokenName = refreshTokenName;
        }

        String token = "";

        // 쿠키에 저장된 데이터 모두 가져오기
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie key : request.getCookies()) {
                if (key.getName().equals(accessTokenName)) {
                    token = key.getValue();
                    break;
                }
            }
        }

        log.info(this.getClass().getName() + ".resolveToken end");
        return token;
    }

    /**
     * JWT 토큰(AccessToken, RefreshToken) 상태 확인
     * @param token 토큰
     * @return 상태정보(EXPIRED, ACCESS, DENIED)
     */
    public JwtStatus validateToken(String token) {
        if (token.length() > 0) {
            try {
                Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

                // 토큰 만료 여부 체크
                // JWT 토큰 생성 시 Exp에 저장한 날짜 값과 비교 검사
                if (claims.getBody().getExpiration().before(new Date())) {
                    return JwtStatus.EXPIRED; // 만료 토큰
                } else {
                    return JwtStatus.ACCESS; // 유효 토큰
                }
            } catch (ExpiredJwtException e) {
                // 만료 토큰인 경우 refresh token을 확인하기 위해
                return JwtStatus.EXPIRED; // 혹시 몰라 한번 더 체크 기간 만료

            } catch (JwtException | IllegalArgumentException e) {
                log.info("jwtException : {}", e);
                return JwtStatus.DENIED; // 유효하지 않음

            }
        } else {
            return JwtStatus.DENIED; // 유효하지 않음
        }
    }


}
