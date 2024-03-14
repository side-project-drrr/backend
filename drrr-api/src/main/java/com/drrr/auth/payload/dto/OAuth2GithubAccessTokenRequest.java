package com.drrr.auth.payload.dto;

import lombok.Builder;


@Builder
public record OAuth2GithubAccessTokenRequest(
        String clientId,
        String clientSecret,
        String code,
        String uri
) {
}
