package com.drrr.auth.payload.request;

public record VerificationRequest (
        String providerId,
        String verificationCode
){
}
