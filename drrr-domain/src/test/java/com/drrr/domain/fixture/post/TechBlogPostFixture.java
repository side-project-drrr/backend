package com.drrr.domain.fixture.post;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TechBlogPostFixture {
    private final static int POST_COUNT = 50;

    public static List<TechBlogPost> createTechBlogPosts() {
        return IntStream.range(0, POST_COUNT).mapToObj(i -> TechBlogPost.builder()
                .title("title" + i)
                .author("author" + i)
                .like(i)
                .summary("summary" + i)
                .url("url" + i)
                .writtenAt(LocalDate.now())
                .urlSuffix("urlSuffix" + i)
                .thumbnailUrl("thumbnailUrl" + i)
                .aiSummary("aiSummary" + i)
                .viewCount(i)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build()).collect(Collectors.toList());
    }
}
