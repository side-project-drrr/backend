package com.drrr.category.request;

import com.drrr.web.page.request.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategorySearchWordRequest {
    @NotNull
    private String keyword;
    @NotNull
    private PageableRequest pageable;
}
