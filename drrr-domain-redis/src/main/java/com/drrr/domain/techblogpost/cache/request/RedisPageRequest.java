package com.drrr.domain.techblogpost.cache.request;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record RedisPageRequest(
        int page,
        int size
) implements Serializable {
    public static RedisPageRequest from(final int page, final int size) {
        return RedisPageRequest.builder()
                .page(page)
                .size(size)
                .build();
    }
}
