package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.RecommendPostService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.DynamicDataService;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import java.util.List;
import java.util.Set;
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
    private final RedisTechBlogPostService redisTechBlogPostService;
    private final TechBlogPostLikeService techBlogPostLikeService;
    private final DynamicDataService dynamicDataService;
    private final String RECOMMENDATION_MEMBER = "recommendation:member:%s";
    private final String REDIS_MEMBER_RECOMMENDATION_POST = "recommendation:posts:member:%s";

    @Transactional
    public List<TechBlogPostResponse> execute(final Long memberId, final int count) {

        if (redisTechBlogPostService.hasCachedKey(memberId, count, RECOMMENDATION_MEMBER)) {
            final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);
            List<RedisSlicePostsContents> memberRecommendation = redisTechBlogPostService.findRedisZSetByKey(
                    memberId, REDIS_MEMBER_RECOMMENDATION_POST);
            return TechBlogPostResponse.fromRedis(memberRecommendation, memberLikedPostIdSet);
        }

        //사용자 가중치 검증
        weightValidationService.validateWeight(memberId);

        //추천 게시물 ids 반환받음
        final List<Long> postIds = recommendPostService.recommendPosts(memberId, count);

        //redis에서 조회
        final List<TechBlogPostCategoryDto> categorizedPosts = techBlogPostService.categorize(postIds);

        //로그 쌓기
        logUpdateService.insertTodayMemberPostRecommendLog(memberId, postIds);

        //사용자 게시물 좋아요 여부
        final List<TechBlogPostLike> memberLikedPosts = techBlogPostLikeService.findMemberLikedPosts(memberId, postIds);

        redisTechBlogPostService.saveRedisRecommendationPost(memberId, categorizedPosts, memberLikedPosts,
                RECOMMENDATION_MEMBER);

        return TechBlogPostResponse.from(categorizedPosts, TechBlogPostLike.toSet(memberLikedPosts));
    }
}
