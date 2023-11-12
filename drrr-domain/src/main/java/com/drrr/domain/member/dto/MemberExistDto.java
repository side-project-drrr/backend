package com.drrr.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record MemberExistDto(
        boolean isRegistered,
        Long memberId
) {
}
