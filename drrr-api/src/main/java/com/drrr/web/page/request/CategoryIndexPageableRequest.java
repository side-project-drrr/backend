package com.drrr.web.page.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;

@Builder
public record CategoryIndexPageableRequest(

        @NotNull
        int page,
        @Max(value = 200, message = "Size must not exceed 10")
        @NotNull
        int size
) {
    public PageRequest fromPageRequest() {
        return PageRequest.of(this.page, this.size);
    }
}
