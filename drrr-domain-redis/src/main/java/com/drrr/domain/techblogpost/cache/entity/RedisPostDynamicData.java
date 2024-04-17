package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisPostDynamicData implements Serializable {
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
