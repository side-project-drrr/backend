package com.drrr.domain.techblogpost.cache;

import com.drrr.domain.techblogpost.cache.request.RedisPageRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisKeywordPostRequest(
        Long categoryId,
        int page,
        boolean hasNext,
        int size,
        List<TechBlogPostCategoryDto> posts
) {
    public static RedisKeywordPostRequest from(final int page, final int size, final Long categoryId,
                                               final boolean hasNext, final List<TechBlogPostCategoryDto> posts) {
        return RedisKeywordPostRequest.builder()
                .categoryId(categoryId)
                .page(page)
                .hasNext(hasNext)
                .size(size)
                .posts(posts)
                .build();
    }

    public RedisPageRequest fromPageRequest() {
        return RedisPageRequest.from(page, size);
    }
}
