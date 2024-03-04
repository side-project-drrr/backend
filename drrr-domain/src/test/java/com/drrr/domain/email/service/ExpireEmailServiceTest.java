package com.drrr.domain.email.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.drrr.domain.email.entity.Email;
import com.drrr.domain.email.repository.EmailRepository;
import com.drrr.domain.exception.DomainException;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.fixture.email.EmailFixture;
import com.drrr.domain.util.ServiceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


class ExpireEmailServiceTest extends ServiceIntegrationTest {

    @Autowired
    private ExpireEmailService expireEmailService;

    @Autowired
    private EmailRepository emailRepository;


    @Transactional
    @ParameterizedTest
    @ValueSource(ints = {181, 182})
        // 3분 1초, 3분 2초
    void 이메일_인증_코드_만료시간을_넘어서면_오류가_발생하고_기존_코드는_삭제됩니다(int seconds) {
        final Email email = emailRepository.save(EmailFixture.create("provider"));

        assertThatThrownBy(() -> expireEmailService.removeWhenExpireTime(
                email.getProviderId(),
                email.getCreatedAt().plusSeconds(seconds)
        )).isInstanceOf(DomainException.class).satisfies((exception) -> {
            final DomainException domainException = (DomainException) exception;
            assertThat(DomainExceptionCode.EMAIL_VERIFICATION_CODE_EXPIRED.getCode())
                    .isEqualTo(domainException.getCode());
        });
        assertThat(emailRepository.existsByProviderId(email.getProviderId())).isFalse();

    }

    @Transactional
    @Test
    void 이메일_인증코드가_일치하면_기존_코드가_삭제됩니다() {
        final Email email = emailRepository.save(EmailFixture.create("provider", "code"));

        final boolean isRemoved = expireEmailService.removeWhenMatchCode(email.getProviderId(), "code");

        assertAll(
                () -> assertThat(isRemoved).isTrue(),
                () -> assertThat(emailRepository.existsByProviderId(email.getProviderId())).isFalse()
        );
    }

    @Transactional
    @Test
    void 이메일_인증코드가_일치하지_않는_경우_코드가_삭제되지_않습니다() {
        final Email email = emailRepository.save(EmailFixture.create("provider", "code"));

        final boolean isRemoved = expireEmailService.removeWhenMatchCode(email.getProviderId(), "otherCode");

        assertAll(
                () -> assertThat(isRemoved).isFalse(),
                () -> assertThat(emailRepository.existsByProviderId(email.getProviderId())).isTrue()
        );
    }


}