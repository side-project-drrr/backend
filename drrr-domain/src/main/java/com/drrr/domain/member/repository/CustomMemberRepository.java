package com.drrr.domain.member.repository;

import com.drrr.domain.member.dto.MemberDto;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomMemberRepository {
    MemberDto findMemberProfile(final Long memberId);
}
