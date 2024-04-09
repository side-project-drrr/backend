package com.drrr.techblogpost.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberLikedPostsResponse(
        Long memberId,
        List<Long> postIds
) {
    public static MemberLikedPostsResponse from(final List<Long> postIds, final Long memberId) {
        return MemberLikedPostsResponse.builder()
                .memberId(memberId)
                .postIds(postIds)
                .build();
    }
}
