package com.drrr.category.request;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import com.drrr.web.page.request.PageableRequest;
import lombok.Builder;

@Builder
public record CategoryIndexSliceRequest(
        PageableRequest pageableRequest,
        LanguageConstants language,
        IndexConstants index
) {

}
