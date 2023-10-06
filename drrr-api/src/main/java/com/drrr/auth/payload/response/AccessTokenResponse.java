package com.drrr.auth.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
public record AccessTokenResponse(
        @Schema(description = "재발급된 access token", nullable = false, example = "JWT token")
        String accessToken
) {

}
