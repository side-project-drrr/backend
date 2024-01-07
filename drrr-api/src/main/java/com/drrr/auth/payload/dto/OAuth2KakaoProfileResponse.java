package com.drrr.auth.payload.dto;

import lombok.Builder;

@Builder
public record OAuth2KakaoProfileResponse(
        String id,
        String profileImage
) {
}
