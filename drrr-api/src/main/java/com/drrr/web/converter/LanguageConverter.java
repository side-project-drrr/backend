package com.drrr.web.converter;

import com.drrr.core.category.constant.CategoryTypeConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LanguageConverter implements Converter<String, CategoryTypeConstants> {
    @Override
    public CategoryTypeConstants convert(String source) {
        return CategoryTypeConstants.valueOf(source.toUpperCase());
    }

}
