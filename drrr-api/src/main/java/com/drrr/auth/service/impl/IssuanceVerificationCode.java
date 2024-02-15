package com.drrr.auth.service.impl;

import com.drrr.auth.payload.request.EmailRequest;
import com.drrr.auth.payload.response.EmailCheckResponse;
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

    public EmailCheckResponse execute(final EmailRequest emailRequest) {
        String email = emailRequest.email();
        boolean isDuplicate = memberRepository.existsByEmail(email);

        if (!isDuplicate) {
            String code = verificationService.createVerificationCode(emailRequest.providerId(), emailRequest.email());
            emailProducer.sendVerificationMessage(emailRequest.email(), code);
        }
        
        return EmailCheckResponse.builder()
                .email(email)
                .isDuplicate(isDuplicate)
                .build();
    }

}
