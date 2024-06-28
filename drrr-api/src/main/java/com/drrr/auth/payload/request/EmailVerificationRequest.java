package com.drrr.auth.payload.request;

public record EmailVerificationRequest(
        String providerId,
        String verificationCode
) {
}
