package com.drrr.domain.category.cache;

import com.drrr.domain.category.dto.CategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisCategory", timeToLive = 3600) // Redis Repository 사용을 위한
@Builder
public record RedisCategory(
        @Id
        Long id,
        String name
) implements Serializable {
    public static List<RedisCategory> from(final List<CategoryDto> categoryDto) {
        return categoryDto.stream()
                .map(category -> RedisCategory.builder()
                        .id(category.id())
                        .name(category.name())
                        .build())
                .toList();
    }
}

