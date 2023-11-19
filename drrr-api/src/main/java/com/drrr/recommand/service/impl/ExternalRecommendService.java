package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.RecommendPostService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.recommand.dto.RecommendResponse;
import com.drrr.recommand.dto.TechBlogPostDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalRecommendService {

    private final RecommendPostService recommendPostService;
    private final WeightValidationService weightValidationService;

    @Transactional
    public RecommendResponse execute(final Long memberId) {
        //두번 째 파라미터의 now는 가중치 검증 test를 진행하기 위함
        weightValidationService.validateWeight(memberId);
        final List<TechBlogPost> posts = recommendPostService.recommendPosts(memberId);
        return RecommendResponse.builder()
                .posts(posts.stream()
                        .map(post -> TechBlogPostDto.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .createdDate(post.getCreatedDate())
                                .url(post.getUrl())
                                .urlSuffix(post.getUrlSuffix())
                                .summary(post.getSummary())
                                .techBlogCode(post.getTechBlogCode())
                                .thumbnailUrl(post.getThumbnailUrl())
                                .viewCount(post.getViewCount())
                                .build()).toList())
                .build();
    }
}
