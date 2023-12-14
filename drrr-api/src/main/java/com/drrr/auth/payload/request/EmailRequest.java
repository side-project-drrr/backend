package com.drrr.auth.payload.request;

import jakarta.validation.constraints.NotNull;


public record EmailRequest(
        @NotNull String providerId,
        @NotNull String email
) {
}
