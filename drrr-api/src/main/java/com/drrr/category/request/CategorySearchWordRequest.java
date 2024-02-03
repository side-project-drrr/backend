package com.drrr.category.request;

import lombok.Builder;

@Builder
public record CategorySearchWordRequest(
        int page,
        int size,
        String sort,
        String direction,
        String searchWord
) {
}
