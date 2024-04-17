package com.drrr.domain.category.cache;

import com.drrr.domain.category.dto.CategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisCategory(
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

