package com.drrr.domain.member.repository.impl;

import static com.drrr.domain.member.entity.QMember.member;

import com.drrr.domain.member.dto.MemberDto;
import com.drrr.domain.member.repository.CustomMemberRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public MemberDto findMemberProfile(final Long memberId) {
        return queryFactory.select(Projections.constructor(MemberDto.class
                        , member.id
                        , member.email
                        , member.nickname
                        , member.profileImageUrl
                        , member.provider
                        , member.providerId)
                ).from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }
}
