package com.drrr.member.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.dto.MemberDto;
import com.drrr.domain.member.service.SearchMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExternalMemberService {
    private final SearchMemberService searchMemberService;

    public MemberDto execute(final Long memberId) {
        final var member = searchMemberService.execute(memberId);

        if (member.isActive()) {
            return MemberDto.toDto(member);
        }

        throw DomainExceptionCode.MEMBER_ACCOUNT_DEACTIVATED.newInstance();
    }

}
