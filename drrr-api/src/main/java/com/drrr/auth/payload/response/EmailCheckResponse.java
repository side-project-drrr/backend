package com.drrr.auth.payload.response;

import lombok.Builder;

@Builder
public record EmailCheckResponse(
        String email,
        boolean isDuplicate
) {
}
