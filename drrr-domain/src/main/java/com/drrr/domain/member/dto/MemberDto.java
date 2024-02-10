package com.drrr.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record MemberDto(
        Long id,
        String email,
        String nickname,
        String profileImageUrl,
        String provider,
        String providerId
) {
    @QueryProjection
    public MemberDto(Long id, String email, String nickname, String profileImageUrl, String provider,
                     String providerId) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
        this.providerId = providerId;
    }
}
