package com.drrr.domain.member.repository.impl;

import static com.drrr.domain.member.entity.QMember.member;

import com.drrr.domain.member.dto.MemberDto;
import com.drrr.domain.member.repository.CustomMemberRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {
    private final EntityManager em;

    @Override
    public void updateMemberProfile(Long memberId, String nickname, String email) {
        JPAUpdateClause updateClause = new JPAUpdateClause(em, member);

        if (Objects.nonNull(nickname)) {
            updateClause.set(member.nickname, nickname);
        }
        if (Objects.nonNull(email)) {
            updateClause.set(member.email, email);
        }

        updateClause.where(member.id.eq(memberId)).execute();
    }
}
