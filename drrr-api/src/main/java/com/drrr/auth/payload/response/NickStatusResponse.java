package com.drrr.auth.payload.response;

import lombok.Builder;

@Builder
public record NickStatusResponse(
        boolean isDuplicate
) {
}
