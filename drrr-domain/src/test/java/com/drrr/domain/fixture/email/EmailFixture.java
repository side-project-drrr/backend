package com.drrr.domain.fixture.email;

import com.drrr.domain.email.entity.Email;

public class EmailFixture {

    public static Email create(String providerId) {
        return Email.builder()
                .email("random email")
                .providerId(providerId)
                .isVerified(false)
                .build();
    }

    public static Email create(String providerId, String verificationCode) {
        return Email.builder()
                .email("random email")
                .providerId(providerId)
                .verificationCode(verificationCode)
                .isVerified(false)
                .build();
    }
}
