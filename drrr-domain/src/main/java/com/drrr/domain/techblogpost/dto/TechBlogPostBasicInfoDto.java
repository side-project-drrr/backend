package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TechBlogPostBasicInfoDto(
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        int viewCount,
        int postLike,
        LocalDate writtenAt

) {
    @QueryProjection
    public TechBlogPostBasicInfoDto(Long id, String title, String summary, TechBlogCode techBlogCode,
                                    String thumbnailUrl,
                                    int viewCount, int postLike, LocalDate writtenAt) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.techBlogCode = techBlogCode;
        this.thumbnailUrl = thumbnailUrl;
        this.viewCount = viewCount;
        this.postLike = postLike;
        this.writtenAt = writtenAt;
    }

}
