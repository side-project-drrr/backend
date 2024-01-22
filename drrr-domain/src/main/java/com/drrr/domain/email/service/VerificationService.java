package com.drrr.domain.email.service;

import com.drrr.domain.email.entity.Email;
import com.drrr.domain.email.repository.EmailRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
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
    private final EmailRepository emailRepository;
    private static final int END_INDEX = 6;
    private static final int THREE_MINUTES = 180;

    public String createVerificationCode(final String providerId, final String email) {
        boolean codeExists = emailRepository.existsByProviderId(providerId);

        //인증번호를 중복으로 요청하는 경우 기존 것 삭제
        if (codeExists) {
            emailRepository.deleteByProviderId(providerId);
        }

        final UUID uuid = UUID.randomUUID();
        final String code = uuid.toString().replace("-", "").substring(0, END_INDEX);

        //isVerified : 프론트에게 추후 인증코드가 유효한지 여부 전달하기 위함
        final Email emailEntity = Email.builder()
                .email(email)
                .verificationCode(code)
                .isVerified(false)
                .providerId(providerId)
                .build();
        emailRepository.save(emailEntity);
        return code;
    }

    public VerificationDto verifyCode(final String providerId, final String verificationCode) {
        final Email email = emailRepository.findByProviderId(providerId).orElseThrow(() -> {
            log.error("provider id로 이메일 인증 정보를 찾을 수 없습니다.");
            log.error("providerId -> {}", providerId);
            return DomainExceptionCode.EMAIL_VERIFICATION_INFORMATION_NOT_FOUND.newInstance();
        });

        final Duration duration = Duration.between(email.getCreatedAt(), LocalDateTime.now());
        final long differenceInSeconds = duration.toSeconds();
        boolean isVerified = Objects.equals(email.getVerificationCode(), verificationCode);

        //3분 안에 입력
        if (differenceInSeconds > THREE_MINUTES) {
            //기존 저장 인증번호 삭제
            emailRepository.deleteByProviderId(providerId);

            log.error("이메일 인증 정보가 만료되었습니다.");
            log.error("providerId -> {}", providerId);
            throw DomainExceptionCode.EMAIL_VERIFICATION_CODE_EXPIRED.newInstance();
        }

        final VerificationDto verificationDto = VerificationDto.builder()
                .isVerified(isVerified)
                .providerId(providerId)
                .build();

        //인증이 완료되었다면 기존 기록 삭제
        if (isVerified) {
            emailRepository.deleteByProviderId(providerId);
        }

        return verificationDto;

    }

    @Builder
    public record VerificationDto(
            String providerId,
            boolean isVerified
    ) {

    }
}
