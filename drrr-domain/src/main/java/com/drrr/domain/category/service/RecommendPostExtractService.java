package com.drrr.domain.category.service;

import com.drrr.domain.category.domain.CategoryPostDistribution;
import com.drrr.domain.category.domain.CategoryWeights;
import com.drrr.domain.category.dto.ExtractedPostCategoryDto;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepositoryImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecommendPostExtractService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final CustomTechBlogPostCategoryRepositoryImpl customTechBlogPostCategoryRepository;

    public List<Long> extractRecommendPostIds(
            final int requirePostCount,
            final Long memberId,
            final List<Long> todayUnreadRecommendPostIds
    ) {
        //카테고리_가중치 Mapping Table를 특정 MemberId로 조회
        final CategoryWeights categoryWeights = new CategoryWeights(categoryWeightRepository.findByMemberId(memberId));

        final List<Long> categoryIds = categoryWeights.extractCategoryIds();

        //카테고리별로 할당해야 하는 게시물 Map
        //distributionMap -> key : categoryId, value : 할당해야 하는 기술블로그 개수
        final List<ExtractedPostCategoryDto> extractedPostsCategories = customTechBlogPostCategoryRepository.findPostsByCategoryIdsNotInLog(
                categoryIds,
                memberId
        );

        final CategoryPostDistribution distribution = new CategoryPostDistribution(
                categoryWeights.calculatePostDistribution(requirePostCount));

        //추천할 게시물 ids 추출
        Set<Long> postIds = distribution.extractRecommendPostIds(extractedPostsCategories, requirePostCount);

        //오늘 추천은 받았으나 안 읽었던 추천 게시물과 추천해줘야 하는 게시물을 합쳐서 request의 count만큼 반환
        postIds.addAll(todayUnreadRecommendPostIds);

        return new ArrayList<>(postIds);
    }
}
