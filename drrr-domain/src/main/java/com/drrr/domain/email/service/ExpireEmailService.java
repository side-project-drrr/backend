package com.drrr.domain.email.service;


import com.drrr.domain.email.entity.Email;
import com.drrr.domain.email.repository.EmailRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExpireEmailService {
    private final EmailRepository emailRepository;

    public void removeWhenExpireTime(String providerId, LocalDateTime now) {
        Email email = findByProviderId(providerId);

        if (email.validateExpire(now)) {
            emailRepository.deleteByProviderId(email.getProviderId());
            log.error("이메일 인증 정보가 만료되었습니다. provierId -> {}", email.getProviderId());
            throw DomainExceptionCode.EMAIL_VERIFICATION_CODE_EXPIRED.newInstance();
        }
    }


    public boolean removeWhenMatchCode(String providerId, String verificationCode) {
        Email email = findByProviderId(providerId);
        if (email.matchVerificationCode(verificationCode)) {
            log.info("이메일 인증 정보가 만료 되었습니다. providerId -> {}", email.getProviderId());
            emailRepository.deleteByProviderId(email.getProviderId());
            return true;
        }
        return false;
    }

    private Email findByProviderId(final String providerId) {
        return emailRepository.findByProviderId(providerId).orElseThrow(() -> {
            log.error("provider id로 이메일 인증 정보를 찾을 수 없습니다. providerId -> {}", providerId);
            return DomainExceptionCode.EMAIL_VERIFICATION_INFORMATION_NOT_FOUND.newInstance();
        });
    }

}
