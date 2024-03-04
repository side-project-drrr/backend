package com.drrr.domain.email.service;

import com.drrr.domain.email.entity.Email;
import com.drrr.domain.email.generator.EmailCodeGenerator;
import com.drrr.domain.email.repository.EmailRepository;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class VerificationService {

    private final EmailCodeGenerator emailCodeGenerator;
    private final ExpireEmailService expireEmailService;
    private final EmailRepository emailRepository;

    public String createVerificationCode(final String providerId, final String email) {
        this.removeExistsVerificationCode(providerId);

        return emailRepository.save(Email.builder()
                .email(email)
                .verificationCode(emailCodeGenerator.generate())
                .isVerified(false)
                .providerId(providerId)
                .build()).getVerificationCode();
    }

    private void removeExistsVerificationCode(String providerId) {
        if (emailRepository.existsByProviderId(providerId)) {
            emailRepository.deleteByProviderId(providerId);
        }
    }

    public VerificationDto verifyCode(final String providerId, final String verificationCode) {
        expireEmailService.removeWhenExpireTime(providerId, LocalDateTime.now());
        final boolean isRemoved = expireEmailService.removeWhenMatchCode(providerId, verificationCode);

        return VerificationDto.builder()
                .isVerified(isRemoved)
                .providerId(providerId)
                .build();

    }

    @Builder
    public record VerificationDto(
            String providerId,
            boolean isVerified
    ) {

    }
}
