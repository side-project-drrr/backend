package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisPostDynamicData", timeToLive = 3600)
@Builder
public record RedisPostDynamicData(
        @Id
        Long postId,
        int viewCount,
        int likeCount
) implements Serializable {
    public static List<RedisPostDynamicData> from(final List<TechBlogPostCategoryDto> contents) {
        return contents.stream()
                .map(content -> RedisPostDynamicData.builder()
                        .postId(content.techBlogPostStaticDataDto().id())
                        .likeCount(content.techBlogPostDynamicDto().likeCount())
                        .viewCount(content.techBlogPostDynamicDto().viewCount())
                        .build())
                .toList();
    }
}
