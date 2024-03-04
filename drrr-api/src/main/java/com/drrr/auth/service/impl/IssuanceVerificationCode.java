package com.drrr.auth.service.impl;

import static com.drrr.domain.exception.DomainExceptionCode.EMAIL_VERIFICATION_CODE_EXPIRED;

import com.drrr.auth.payload.request.EmailRequest;
import com.drrr.domain.email.service.VerificationService;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.infra.notifications.kafka.email.EmailProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssuanceVerificationCode {
    private final VerificationService verificationService;
    private final EmailProducer emailProducer;
    private final MemberRepository memberRepository;

    public void execute(final EmailRequest emailRequest) {
        EMAIL_VERIFICATION_CODE_EXPIRED.invokeByCondition(
                memberRepository.existsByEmail(emailRequest.email())
        );

        emailProducer.sendVerificationMessage(
                emailRequest.email(),
                verificationService.createVerificationCode(emailRequest.providerId(), emailRequest.email())
        );
    }

}
