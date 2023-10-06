package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.RecommendPostService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.recommand.dto.RecommendResponse;
import com.drrr.recommand.dto.TechBlogPostDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalRecommendService {

    private final RecommendPostService recommendPostService;
    private final WeightValidationService weightValidationService;
    private final LogUpdateService logUpdateService;

    public RecommendResponse execute(Long memberId) {
        //두번 째 파라미터의 now는 가중치 검증 test를 진행하기 위함
        weightValidationService.validateWeight(memberId, LocalDateTime.now());
        List<TechBlogPost> posts = recommendPostService.recommendPosts(memberId);
        logUpdateService.updateMemberPostRecommendLog(memberId, posts);
        return RecommendResponse.builder()
                .posts(posts.stream()
                        .map(post -> TechBlogPostDto.builder()
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
