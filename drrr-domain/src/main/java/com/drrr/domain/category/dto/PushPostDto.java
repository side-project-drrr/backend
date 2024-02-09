package com.drrr.domain.category.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PushPostDto(
        List<Long> postIds,
        Long memberId
) {
}
