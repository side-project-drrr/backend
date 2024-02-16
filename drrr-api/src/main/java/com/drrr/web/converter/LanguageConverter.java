package com.drrr.web.converter;

import com.drrr.core.category.constant.LanguageConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LanguageConverter implements Converter<String, LanguageConstants> {
    @Override
    public LanguageConstants convert(String source) {
        return LanguageConstants.valueOf(source.toUpperCase());
    }

}
