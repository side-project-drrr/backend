package com.drrr.auth.service.impl;

import com.drrr.auth.payload.request.EmailRequest;
import com.drrr.domain.email.service.VerificationService;
import com.drrr.infra.notifications.kafka.email.EmailProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssuanceVerificationCode {
    private final VerificationService verificationService;
    private final EmailProducer emailProducer;

    public void execute(final EmailRequest emailRequest){
        String code = verificationService.createVerificationCode(emailRequest.providerId(), emailRequest.email());
        emailProducer.sendVerificationMessage(emailRequest.email(), code);
    }

}
