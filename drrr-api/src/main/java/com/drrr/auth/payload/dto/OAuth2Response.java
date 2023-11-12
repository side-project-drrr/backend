package com.drrr.auth.payload.dto;

import lombok.Builder;

@Builder
public record OAuth2Response(
        String providerId,
        boolean isRegistered
) {
}
