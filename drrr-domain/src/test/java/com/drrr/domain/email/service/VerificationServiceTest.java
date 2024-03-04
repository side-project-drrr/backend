package com.drrr.domain.email.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import com.drrr.domain.email.repository.EmailRepository;
import com.drrr.domain.util.ServiceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class VerificationServiceTest extends ServiceIntegrationTest {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private EmailRepository emailRepository;


    @Test
    void 랜덤한_이메일_코드가_정상적으로_생성됩니다() {
        given(emailCodeGenerator.generate()).willReturn("random");

        assertThat(verificationService.createVerificationCode("none", "email@email.com"))
                .isEqualTo("random");
    }


    @Test
    void 동일한_요청이_발생하는_경우_기존_코드가_삭제됩니다() {
        given(emailCodeGenerator.generate()).willReturn("random1", "random2");

        final String previousCode = verificationService.createVerificationCode("none", "email@email.com");
        final String newCode = verificationService.createVerificationCode("none", "email@email.com");

        final String actual = emailRepository.findByProviderId("none")
                .orElseThrow()
                .getVerificationCode();

        assertAll(
                () -> assertThat(previousCode).isNotEqualTo(actual),
                () -> assertThat(newCode).isEqualTo(actual)
        );
    }

    @Test
    void 이메일_인증_코드가_일치합니다() {
        given(emailCodeGenerator.generate()).willReturn("random");

        final String code = verificationService.createVerificationCode("provider", "email");

        assertThat(verificationService.verifyCode("provider", code).isVerified()).isTrue();
    }

    @Test
    void 이메일_인증_코드가_일치하지_않습니다() {
        given(emailCodeGenerator.generate()).willReturn("random");

        final String code = verificationService.createVerificationCode("provider", "email");

        assertThat(verificationService.verifyCode("provider", "other").isVerified()).isFalse();
    }


}