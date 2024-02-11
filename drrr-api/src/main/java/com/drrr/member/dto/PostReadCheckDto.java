package com.drrr.member.dto;

import lombok.Builder;

@Builder
public record PostReadCheckDto(
        Long postId,
        boolean isRead
) {
}
