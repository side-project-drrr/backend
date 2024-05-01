package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.RecommendPostService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.recommend.service.RedisRecommendationService;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalRecommendService {

    private final RecommendPostService recommendPostService;
    private final WeightValidationService weightValidationService;
    private final TechBlogPostService techBlogPostService;
    private final LogUpdateService logUpdateService;
    private final RedisRecommendationService redisRecommendationService;

    @Transactional
    public List<TechBlogPostResponse> execute(final Long memberId, final int count) {

        if (redisRecommendationService.hasCachedKey(memberId)) {
            List<RedisSlicePostsContents> memberRecommendation = redisRecommendationService.findMemberRecommendation(
                    memberId);
            return TechBlogPostResponse.fromRedis(memberRecommendation);
        }

        //사용자 가중치 검증
        weightValidationService.validateWeight(memberId);

        //추천 게시물 ids 반환받음
        final List<Long> postIds = recommendPostService.recommendPosts(memberId, count);

        //redis에서 조회
        List<TechBlogPostCategoryDto> categorizedPosts = techBlogPostService.categorize(postIds);

        //로그 쌓기
        logUpdateService.insertTodayMemberPostRecommendLog(memberId, postIds);

        redisRecommendationService.saveMemberRecommendation(memberId, categorizedPosts);

        return TechBlogPostResponse.from(categorizedPosts);
    }
}
