package com.drrr.web.converter;

import com.drrr.core.category.constant.IndexConstants;
import org.springframework.core.convert.converter.Converter;

public class IndexConverter implements Converter<String, IndexConstants> {
    @Override
    public IndexConstants convert(String source) {
        return IndexConstants.valueOf(source.toUpperCase());
    }
}
