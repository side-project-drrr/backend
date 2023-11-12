package com.drrr.auth.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class  OAuth2GithubAccessTokenRequest{
    private String clientId;
    private String clientSecret;
    private String code;
    private String uri;



    public static OAuth2GithubAccessTokenRequest buildOAuth2GithubAccessTokenRequest(final String clientId,  final String code, final String clientSecret, final String uri){

        return OAuth2GithubAccessTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .uri(uri)
                .build();
    }
}
