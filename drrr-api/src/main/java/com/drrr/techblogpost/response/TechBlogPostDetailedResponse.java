package com.drrr.techblogpost.response;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TechBlogPostDetailedResponse(
        Long id,
        String title,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        String aiSummary,
        LocalDate writtenDate,
        int viewCount,
        int postLikeCount,
        String author,
        String url
) {
    public static TechBlogPostDetailedResponse from(final TechBlogPostDetailedInfoDto post) {
        return TechBlogPostDetailedResponse.builder()
                .aiSummary(post.aiSummary())
                .techBlogCode(post.techBlogCode())
                .id(post.id())
                .thumbnailUrl(post.thumbnailUrl())
                .title(post.title())
                .writtenDate(post.writtenDate())
                .viewCount(post.viewCount())
                .postLikeCount(post.postLikeCount())
                .author(post.author())
                .url(post.url())
                .build();

    }
}
