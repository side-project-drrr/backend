package com.drrr.web.converter;

import com.drrr.core.code.techblog.TopTechBlogType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToTopTechBlogTypeConverter implements Converter<String, TopTechBlogType> {
    @Override
    public TopTechBlogType convert(String source) {
        return TopTechBlogType.valueOf(source.toUpperCase());
    }
}