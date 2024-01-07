package com.drrr.auth.payload.dto;

import lombok.Builder;

@Builder
public record OAuth2GithubProfileResponse(
        String id,
        String avatarUrl
) {
}
