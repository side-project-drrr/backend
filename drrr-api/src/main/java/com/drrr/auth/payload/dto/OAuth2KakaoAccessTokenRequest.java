package com.drrr.auth.payload.dto;

import com.nimbusds.oauth2.sdk.GrantType;
import lombok.Builder;
import org.springframework.web.util.UriComponentsBuilder;

@Builder
public record OAuth2KakaoAccessTokenRequest(
        String code,
        String redirectUrl,
        String clientId,
        String uri
) {

    public String toUrl() {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri())
                .queryParam("grant_type", GrantType.AUTHORIZATION_CODE.getValue())
                .queryParam("client_id", clientId)
                .queryParam("code", code);

        return uriBuilder.build().encode().toUriString();
    }
}
