package com.drrr.auth.payload.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record OAuth2GithubBody(
        String clientId,
        String clientSecret,
        String code
) {
    static public OAuth2GithubBody from(final OAuth2GithubAccessTokenRequest requestBody) {
        return OAuth2GithubBody.builder()
                .clientId(requestBody.clientId())
                .clientSecret(requestBody.clientSecret())
                .code(requestBody.code())
                .build();
    }
}
