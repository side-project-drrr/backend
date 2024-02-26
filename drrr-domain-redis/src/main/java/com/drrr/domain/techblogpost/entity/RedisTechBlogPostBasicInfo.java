package com.drrr.domain.techblogpost.entity;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RedisTechBlogPostBasicInfo(
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        int viewCount,
        int postLike,
        LocalDate writtenAt,
        String url

) implements Serializable {
    public static RedisTechBlogPostBasicInfo from(final TechBlogPostBasicInfoDto techBlogPostBasicInfoDto) {
        return RedisTechBlogPostBasicInfo.builder()
                .id(techBlogPostBasicInfoDto.id())
                .title(techBlogPostBasicInfoDto.title())
                .summary(techBlogPostBasicInfoDto.summary())
                .techBlogCode(techBlogPostBasicInfoDto.techBlogCode())
                .thumbnailUrl(techBlogPostBasicInfoDto.thumbnailUrl())
                .viewCount(techBlogPostBasicInfoDto.viewCount())
                .postLike(techBlogPostBasicInfoDto.postLike())
                .writtenAt(techBlogPostBasicInfoDto.writtenAt())
                .url(techBlogPostBasicInfoDto.url())
                .build();
    }

}

