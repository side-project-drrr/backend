package com.drrr.domain.category.service;

import com.drrr.domain.category.domain.CategoryPostDistribution;
import com.drrr.domain.category.domain.CategoryWeights;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepositoryImpl;
import com.drrr.domain.techblogpost.repository.impl.CustomTechBlogPostRepositoryImpl;
import com.querydsl.core.annotations.QueryProjection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecommendPostService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final CustomTechBlogPostRepositoryImpl customTechBlogPostRepository;
    private final CustomTechBlogPostCategoryRepositoryImpl customTechBlogPostCategoryRepository;
    private final MemberPostLogRepository memberPostLogRepository;
    private final PostCategoryUtilityService postCategoryUtilityService;

    @Transactional
    public List<Long> recommendPosts(final Long memberId, int count) {
        //오늘 추천은 받았으나 안 읽었던 추천 게시물 다시 가져와서 반환
        List<Long> todayUnreadRecommendPostIds = memberPostLogRepository.findTodayUnreadRecommendPostIds(memberId);

        int requirePostCount = count;

        //오늘 추천은 받았으나 안 읽었던 추천 게시물 다시 가져와서 반환
        if (requirePostCount == todayUnreadRecommendPostIds.size()){
            return todayUnreadRecommendPostIds;
        }

        //추천해줘야 하는 게시물 수가 오늘 추천은 받았으나 안 읽었던 추천 게시물 수보다 작을 때 안 읽었던 추천 게시물에서 그대로 반환
        if (requirePostCount < todayUnreadRecommendPostIds.size()){
            return todayUnreadRecommendPostIds.subList(0, requirePostCount);
        }

        //오늘 추천은 받았으나 안 읽었던 추천 게시물은 유지하고 추가적으로 추천해줘야 하는 게시물 수를 계산
        requirePostCount -= todayUnreadRecommendPostIds.size();

        //카테고리_가중치 Mapping Table를 특정 MemberId로 조회

        final CategoryWeights categoryWeights = new CategoryWeights(categoryWeightRepository.findByMemberId(memberId));

        List<Long> categoryIds = categoryWeights.extractCategoryIds();

        //카테고리별로 할당해야 하는 게시물 Map
        //distributionMap -> key : categoryId, value : 할당해야 하는 기술블로그 개수
        final List<ExtractedPostCategoryDto> extractedPostsCategories = customTechBlogPostCategoryRepository.findPostsByCategoryIdsNotInLog(
                categoryIds,
                memberId
        );

        CategoryPostDistribution distribution = CategoryPostDistribution.builder()
                .categoryIdToPostCounts(categoryWeights.calculatePostDistribution(requirePostCount))
                .postIds(new HashSet<>())
                .build();


        //추천할 게시물 ids 추출
        Set<Long> postIds = distribution.extractRecommendPostIds(extractedPostsCategories, requirePostCount);

        //오늘 추천은 받았으나 안 읽었던 추천 게시물과 추천해줘야 하는 게시물을 합쳐서 request의 count만큼 반환
        postIds.addAll(todayUnreadRecommendPostIds);

        return postIds.stream().toList();
    }

    @Builder
    public record ExtractedPostCategoryDto(
            Long postId,
            Long categoryId
    ) {
        @QueryProjection
        public ExtractedPostCategoryDto(Long postId, Long categoryId) {
            this.postId = postId;
            this.categoryId = categoryId;
        }
    }
}