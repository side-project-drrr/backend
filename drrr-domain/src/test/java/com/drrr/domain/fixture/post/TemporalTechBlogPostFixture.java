package com.drrr.domain.fixture.post;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import java.time.LocalDate;

public class TemporalTechBlogPostFixture {

    public static TemporalTechBlogPost createTechBlogPost() {
        return TemporalTechBlogPost.builder()
                .title("title")
                .author("author")
                .summary("summary")
                .url("url")
                .urlSuffix("urlSuffix")
                .thumbnailUrl("thumbnailUrl")
                .aiSummary("aiSummary")
                .techBlogCode(TechBlogCode.KAKAO)
                .crawledDate(LocalDate.now())
                .createdDate(LocalDate.now())
                .build();
    }
}
