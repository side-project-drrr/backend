package com.drrr.auth.service.impl;

import com.drrr.auth.payload.response.EmailCheckResponse;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailCheckService {
    private final MemberRepository memberRepository;

    public EmailCheckResponse execute(String email) {
        boolean isDuplicate = memberRepository.existsByEmail(email);
        return EmailCheckResponse.builder()
                .email(email)
                .isDuplicate(isDuplicate)
                .build();
    }
}
