package com.drrr.member.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.dto.MemberDto;
import com.drrr.domain.member.service.MemberProfileUpdateService;
import com.drrr.domain.member.service.SearchMemberService;
import com.drrr.member.payload.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExternalMemberService {
    private final SearchMemberService searchMemberService;
    private final MemberProfileUpdateService memberProfileUpdateService;

    @Transactional(readOnly = true)
    public MemberDto execute(final Long memberId) {
        final var member = searchMemberService.execute(memberId);

        if (member.isActive()) {
            return MemberDto.toDto(member);
        }

        throw DomainExceptionCode.MEMBER_ACCOUNT_DEACTIVATED.newInstance();
    }

    @Transactional
    public void execute(final Long memberId, final ProfileUpdateRequest request){
        memberProfileUpdateService.updateMemberProfile(memberId, request.nickname(), request.email());
    }

}
