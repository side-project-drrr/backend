package com.drrr.auth.payload.dto;

import lombok.Builder;


@Builder
public record OAuth2GithubAccessTokenRequest(
        String clientId,
        String clientSecret,
        String code,
        String uri
) {

    public static OAuth2GithubAccessTokenRequest buildOAuth2GithubAccessTokenRequest(final String clientId,
                                                                                     final String code,
                                                                                     final String clientSecret,
                                                                                     final String uri) {

        return OAuth2GithubAccessTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .uri(uri)
                .build();
    }
}
