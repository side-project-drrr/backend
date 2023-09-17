package com.drrr.recommand.dto;

import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecommendResponse {
    private List<TechBlogPost> posts;
}
