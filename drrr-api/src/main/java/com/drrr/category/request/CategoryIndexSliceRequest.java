package com.drrr.category.request;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryIndexSliceRequest {
    private int page;
    private int size;
    private String sort;
    private String direction;
    private LanguageConstants language;
    private IndexConstants index;

}
