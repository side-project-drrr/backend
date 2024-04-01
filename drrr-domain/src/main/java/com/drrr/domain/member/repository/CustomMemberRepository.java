package com.drrr.domain.member.repository;

import com.drrr.domain.member.dto.MemberDto;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomMemberRepository {
    void updateMemberProfile(final Long memberId, final String nickname, final String email);
}
