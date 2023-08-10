package com.example.drrrbatch.batch.domain;

import com.example.drrrbatch.batch.vo.TechBlogCode;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ExternalBlogPost(
        String link,
        String title,
        String summary,
        String author,
        LocalDate postDate,
        String suffix,
        String thumbnailUrl,
        TechBlogCode code
) {

}
