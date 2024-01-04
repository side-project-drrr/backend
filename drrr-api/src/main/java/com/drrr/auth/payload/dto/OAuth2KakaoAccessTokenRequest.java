package com.drrr.auth.payload.dto;

import lombok.Builder;

@Builder
public record OAuth2KakaoAccessTokenRequest(
        String code,
        String redirectUrl,
        String clientId,
        String uri
)
{
    public static OAuth2KakaoAccessTokenRequest buildOAuth2KakaoAccessTokenRequest(final String clientId, final String code, final String uri) {


        return OAuth2KakaoAccessTokenRequest.builder()
                .code(code)
                .clientId(clientId)
                .uri(uri)
                .build();
    }



}
