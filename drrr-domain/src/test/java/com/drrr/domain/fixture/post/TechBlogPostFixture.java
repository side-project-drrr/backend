package com.drrr.domain.fixture.post;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TechBlogPostFixture {
    private final static int DEFAULT_LIKES = 100;
    private final static int DEFAULT_VIEW = 100;

    public static TechBlogPost createTechBlogPost() {
        return TechBlogPost.builder()
                .title("title")
                .author("author")
                .like(DEFAULT_LIKES)
                .summary("summary")
                .url("url")
                .writtenAt(LocalDate.now())
                .urlSuffix("urlSuffix")
                .thumbnailUrl("thumbnailUrl")
                .aiSummary("aiSummary")
                .viewCount(DEFAULT_VIEW)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build();
    }

    public static TechBlogPost createTechBlogPost(final int viewCount) {
        return TechBlogPost.builder()
                .title("title")
                .author("author")
                .like(DEFAULT_LIKES)
                .summary("summary")
                .url("url")
                .writtenAt(LocalDate.now())
                .urlSuffix("urlSuffix")
                .thumbnailUrl("thumbnailUrl")
                .aiSummary("aiSummary")
                .viewCount(viewCount)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build();
    }

    public static List<TechBlogPost> createTechBlogPosts(final int count) {
        return IntStream.range(0, count).mapToObj(i -> TechBlogPost.builder()
                .title("title" + i)
                .author("author" + i)
                .like(i)
                .summary("summary" + i)
                .url("url" + i)
                .writtenAt(LocalDate.now())
                .urlSuffix("urlSuffix" + i)
                .thumbnailUrl("thumbnailUrl")
                .aiSummary("aiSummary" + i)
                .viewCount(i)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build()).collect(Collectors.toList());
    }


}
