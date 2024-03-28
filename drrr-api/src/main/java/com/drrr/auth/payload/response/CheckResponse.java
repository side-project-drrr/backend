package com.drrr.auth.payload.response;

import lombok.Builder;

@Builder
public record CheckResponse(
        boolean isDuplicate
) {
}
