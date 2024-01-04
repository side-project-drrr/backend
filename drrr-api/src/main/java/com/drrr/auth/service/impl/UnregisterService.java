package com.drrr.auth.service.impl;

import com.drrr.domain.member.service.UnregisterMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UnregisterService {
    private final UnregisterMemberService unregisterMemberService;

    public void execute(final Long memberId) {
        unregisterMemberService.unregisterMember(memberId);
    }
}
