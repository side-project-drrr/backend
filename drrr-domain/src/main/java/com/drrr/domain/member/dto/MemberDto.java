package com.drrr.domain.member.dto;

import com.drrr.domain.member.entity.Member;
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

    public static MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .provider(member.getProvider())
                .providerId(member.getProviderId())
                .build();
    }
}
