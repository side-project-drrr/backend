package com.drrr.reader.fluent;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.Page;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;


@RequiredArgsConstructor
public class PageItemReader implements ItemReader<ExternalBlogPosts>, TechBlogReader {
    private final Page<ExternalBlogPosts> pages;

    @Getter
    private final TechBlogCode techBlogCode;


    @Override
    public ExternalBlogPosts read() {
        return pages.execute();
    }


}
