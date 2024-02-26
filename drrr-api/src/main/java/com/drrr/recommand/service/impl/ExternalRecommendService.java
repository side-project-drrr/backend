package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.RecommendPostService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
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
    public List<TechBlogPostCategoryDto> execute(final Long memberId) {
        //사용자 가중치 검증
        weightValidationService.validateWeight(memberId);

        //추천 게시물 ids 반환받음
        final List<Long> postIds = recommendPostService.recommendPosts(memberId);

        //redis에서 조회
        final List<TechBlogPost> postsInRedis = redisTechBlogPostService.findPostsByIdsInRedis(postIds);
        final List<TechBlogPost> notCachedPosts = techBlogPostService.findNotCachedTechBlogPosts(postsInRedis, postIds);
        final List<TechBlogPost> posts = new ArrayList<>(postsInRedis);

        //캐싱해야 할 포스트가 있다면
        if (!notCachedPosts.isEmpty()) {
            redisTechBlogPostService.savePostsInRedis(notCachedPosts);
        }

        List<Long> concatPostIds = Stream.concat(posts.stream().map(TechBlogPost::getId)
                , notCachedPosts.stream().map(TechBlogPost::getId)).toList();

        List<TechBlogPostCategoryDto> categorizedPosts = techBlogPostService.categorize(concatPostIds);

        //로그 쌓기
        logUpdateService.insertTodayMemberPostRecommendLog(memberId, postIds);

        return categorizedPosts;
    }
}
