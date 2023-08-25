package com.drrr.drrrbatch.domain;

import com.drrr.drrrjpa.domain.code.TechBlogCode;
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
