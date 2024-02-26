package com.drrr.category.request;

import com.drrr.web.page.request.PageableRequest;
import lombok.Builder;

@Builder
public record CategorySearchWordRequest(
        PageableRequest pageableRequest,
        String keyword
) {
}
