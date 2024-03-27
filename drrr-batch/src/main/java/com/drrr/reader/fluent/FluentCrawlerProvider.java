package com.drrr.reader.fluent;


import com.drrr.core.code.techblog.TechBlogCode;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FluentCrawlerProvider {
    private final Map<TechBlogCode, TechBlogReader> map;


    public TechBlogReader getItemReader(TechBlogCode code) {
        return map.get(code);
    }
}
