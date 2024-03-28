package com.drrr.reader.fluent;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.MultiPage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ParallelPageItemReader implements TechBlogReader {

    private final MultiPage<ExternalBlogPosts> pages;
    @Getter
    private final TechBlogCode techBlogCode;

    @Override
    public ExternalBlogPosts read() {
        var result = pages.execute()
                .stream()
                .flatMap(externalBlogPosts -> externalBlogPosts.posts().stream())
                .collect(collectingAndThen(toList(), ExternalBlogPosts::new));

        if (result.posts().isEmpty()) {
            return null;
        }
        return result;
    }
}
