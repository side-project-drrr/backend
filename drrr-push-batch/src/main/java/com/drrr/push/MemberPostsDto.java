package com.drrr.push;

import lombok.Builder;

@Builder
public record MemberPostsDto(
        Long memberId,
        Long postId
) {
}
