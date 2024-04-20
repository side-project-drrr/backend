package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostStaticDataDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisTechBlogPostStaticData", timeToLive = 3600) // Redis Repository 사용을 위한
@Builder
public record RedisTechBlogPostStaticData(
        @Id
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        LocalDate writtenAt,
        String url

) implements Serializable {
    public static RedisTechBlogPostStaticData from(final TechBlogPostStaticDataDto techBlogPostBasicInfoDto) {
        return RedisTechBlogPostStaticData.builder()
                .id(techBlogPostBasicInfoDto.id())
                .title(techBlogPostBasicInfoDto.title())
                .summary(techBlogPostBasicInfoDto.summary())
                .techBlogCode(techBlogPostBasicInfoDto.techBlogCode())
                .thumbnailUrl(techBlogPostBasicInfoDto.thumbnailUrl())
                .writtenAt(techBlogPostBasicInfoDto.writtenAt())
                .url(techBlogPostBasicInfoDto.url())
                .build();
    }

    public static List<RedisTechBlogPostStaticData> from(final List<TechBlogPost> posts) {
        return posts.stream()
                .map((post) -> RedisTechBlogPostStaticData.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .summary(post.getSummary())
                        .techBlogCode(post.getTechBlogCode())
                        .thumbnailUrl(post.getThumbnailUrl())
                        .writtenAt(post.getWrittenAt())
                        .url(post.getUrl())
                        .build())
                .toList();
    }

}

