package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record TechBlogPostOuterDto(
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        int viewCount,
        int postLike,
        LocalDate writtenAt

) {
    static public List<TechBlogPostOuterDto> from (final List<TechBlogPost> posts){
        return posts.stream()
                .map(post -> TechBlogPostOuterDto.builder()
                        .title(post.getTitle())
                        .techBlogCode(post.getTechBlogCode())
                        .summary(post.getSummary())
                        .postLike(post.getPostLike())
                        .thumbnailUrl(post.getThumbnailUrl())
                        .viewCount(post.getViewCount())
                        .id(post.getId())
                        .writtenAt(post.getWrittenAt())
                        .build())
                .toList();
    }
}
