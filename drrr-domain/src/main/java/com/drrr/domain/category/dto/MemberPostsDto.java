package com.drrr.domain.category.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record MemberPostsDto(
        Long memberId,
        Long postId
) {
    @QueryProjection
    public MemberPostsDto(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }
}
