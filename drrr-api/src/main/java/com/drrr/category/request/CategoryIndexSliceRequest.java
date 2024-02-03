package com.drrr.category.request;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryIndexSliceRequest {
    @Default
    private final int page = 0;
    @Default
    private final int size = 10;
    @Default
    private final String sort = "name";
    @Default
    private final String direction = "ASC";
    private LanguageRequest languageRequest;

    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class LanguageRequest {
        private LanguageConstants language;
        private IndexConstants index;
    }
}
