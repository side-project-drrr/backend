package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisPostDynamicData", timeToLive = 300)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisPostDynamicData implements Serializable {
    @Id
    private Long postId;
    private int viewCount;
    private int likeCount;


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
