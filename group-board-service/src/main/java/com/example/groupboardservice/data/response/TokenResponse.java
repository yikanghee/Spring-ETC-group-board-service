package com.example.groupboardservice.data.response;

import antlr.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;


    public static TokenResponse accessTokenResponse(String accessToken){
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        return tokenResponse;
    }

    public static TokenResponse accessAndRefreshTokenResponse(String accessToken, String refreshToken) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.refreshToken = refreshToken;
        return tokenResponse;
    }
}
