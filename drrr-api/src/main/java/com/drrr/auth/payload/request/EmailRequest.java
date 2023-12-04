package com.drrr.auth.payload.request;

import lombok.Builder;


public record EmailRequest(
        String providerId,
        String email
) {
}
