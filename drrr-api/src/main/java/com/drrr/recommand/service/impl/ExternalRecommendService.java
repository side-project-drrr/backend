package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.RecommendPostService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.recommand.dto.RecommendRequest;
import com.drrr.recommand.dto.RecommendResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalRecommendService {

    private final RecommendPostService recommendPostService;
    private final WeightValidationService weightValidationService;

    public RecommendResponse execute(RecommendRequest request) {
        weightValidationService.validateWeight(request.getMemberId());
        List<TechBlogPost> posts = recommendPostService.recommendPosts(request.getMemberId());
        return new RecommendResponse(posts);
    }
}
