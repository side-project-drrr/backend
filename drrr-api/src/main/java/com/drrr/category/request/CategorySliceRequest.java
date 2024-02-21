package com.drrr.category.request;

import lombok.Builder;

@Builder
public record CategorySliceRequest(
        int page,
        int size
) {
}
