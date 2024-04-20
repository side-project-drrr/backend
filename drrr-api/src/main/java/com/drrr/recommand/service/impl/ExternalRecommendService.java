package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.RecommendPostService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.web.redis.RedisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalRecommendService {

    private final RecommendPostService recommendPostService;
    private final WeightValidationService weightValidationService;
    private final RedisTechBlogPostService redisTechBlogPostService;
    private final TechBlogPostService techBlogPostService;
    private final LogUpdateService logUpdateService;

    @Transactional
    public List<TechBlogPostCategoryDto> execute(final Long memberId, final int count) {
        //사용자 가중치 검증
        weightValidationService.validateWeight(memberId);

        //추천 게시물 ids 반환받음
        final List<Long> postIds = recommendPostService.recommendPosts(memberId, count);

        //redis에서 조회
        List<TechBlogPostCategoryDto> postsInRedis = RedisUtil.redisPostCategoriesEntityToDto(
                redisTechBlogPostService.findRecommendPostsByIdsInRedis(postIds)
        );

        final List<Long> redisPostIds = postsInRedis.stream()
                .map(post -> post.techBlogPostStaticDataDto().id())
                .toList();

        final List<Long> notCachedPostIds = techBlogPostService.findNotCachedTechBlogPosts(redisPostIds, postIds);

        List<TechBlogPostCategoryDto> categorizedPosts = techBlogPostService.categorize(notCachedPostIds);
        categorizedPosts.addAll(postsInRedis);

        //캐싱해야 할 포스트가 있다면
        if (!notCachedPostIds.isEmpty()) {
            redisTechBlogPostService.saveRecommendPostsInRedis(categorizedPosts);
        }

        //로그 쌓기
        logUpdateService.insertTodayMemberPostRecommendLog(memberId, postIds);

        return categorizedPosts;
    }
}
