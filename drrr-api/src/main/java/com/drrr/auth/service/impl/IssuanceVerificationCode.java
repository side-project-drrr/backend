package com.drrr.auth.service.impl;

import static com.drrr.domain.exception.DomainExceptionCode.EMAIL_DUPLICATE_EXCEPTION;

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
        final String email = emailRequest.email();

        EMAIL_DUPLICATE_EXCEPTION.invokeByCondition(memberRepository.existsByEmail(email));

        emailProducer.sendVerificationMessage(
                email,
                verificationService.createVerificationCode(emailRequest.providerId(), emailRequest.email())
        );
    }

}
