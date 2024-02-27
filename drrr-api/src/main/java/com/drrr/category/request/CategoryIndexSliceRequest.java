package com.drrr.category.request;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import com.drrr.web.page.request.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryIndexSliceRequest {
    @NotNull
    private PageableRequest pageable;
    @NotNull
    private LanguageConstants language;
    @NotNull
    private IndexConstants index;
}
