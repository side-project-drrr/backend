package com.drrr.web.page.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;

@Builder
public record PageableRequest(
        @NotNull
        int page,
        @NotNull
        int size
) {
    public PageRequest fromPageRequest() {
        return PageRequest.of(this.page, this.size);
    }
}
