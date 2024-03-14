package com.drrr.auth.payload.request;

import jakarta.validation.constraints.NotNull;

public record OAuth2ProfileRequest(
        @NotNull String code,
        @NotNull String state
) {
}
