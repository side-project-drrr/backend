package com.drrr.domain.category.service;

import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepositoryImpl;
import com.drrr.domain.techblogpost.repository.impl.CustomTechBlogPostRepositoryImpl;
import com.querydsl.core.annotations.QueryProjection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
        final List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        if (categoryWeights.isEmpty()) {
            log.error("카테고리 가중치를 찾을 수 없습니다.");
            log.error("memberId -> {}", memberId);
            throw DomainExceptionCode.CATEGORY_WEIGHT_NOT_FOUND.newInstance();
        }

        List<Long> categoryIds = categoryWeights.stream().map(cw -> cw.getCategory().getId()).toList();

        //사용자에게 추천해줄 수 있는 모든 기술블로그를 사용자에 등록된 모든 카테고리를 기반으로 가져오기
        final List<ExtractedPostCategoryDto> extractedPostsCategories = customTechBlogPostCategoryRepository.findPostsByCategoryIdsNotInLog(
                categoryIds,
                memberId
        );

        //카테고리별로 할당해야 하는 게시물 Map
        //distributionMap -> key : categoryId, value : 할당해야 하는 기술블로그 개수
        final Map<Long, AtomicInteger> distributionMap = postCategoryUtilityService.calculatePostDistribution(
                categoryWeights,
                requirePostCount
        );

        //추천할 게시물 ids 추출
        Set<Long> postIds = extractRecommendPostIds(
                extractedPostsCategories,
                distributionMap,
                requirePostCount
        );

        //오늘 추천은 받았으나 안 읽었던 추천 게시물과 추천해줘야 하는 게시물을 합쳐서 request의 count만큼 반환
        postIds.addAll(todayUnreadRecommendPostIds);
        return postIds.stream().toList();
    }


    private Set<Long> extractRecommendPostIds(
            final List<ExtractedPostCategoryDto> extractedPostsCategories,
            Map<Long, AtomicInteger> categoryIdToPostCounts,
            final int requireCount
    ) {
        //filter에서 postIds에 담기지 못한 게시물은 requireCount(남은 추천해야하는 게시물 수)만큼 forEach에서 담아줌
        Set<Long> postIds = extractedPostsCategories.stream()
                .filter(dto -> {
                    //특정 카테고리에 대해 할당해야 하는 게시물 수
                    AtomicInteger count = categoryIdToPostCounts.getOrDefault(dto.categoryId, new AtomicInteger(0));

                    //카테고리에 대해 할당해줘야하는 게시물이 존재한다면
                    if (count.get() > 0) {
                        categoryIdToPostCounts.put(dto.categoryId, new AtomicInteger(count.get() - 1));
                        return false;
                    }
                    return true;
                })
                .limit(requireCount)
                .map(ExtractedPostCategoryDto::postId)
                .collect(Collectors.toSet());

        if(postIds.size() == requireCount){
            return postIds;
        }

        Set<Long> extraPostIds = extractedPostsCategories.stream()
                .filter(dto -> !postIds.contains(dto.postId))
                .map(ExtractedPostCategoryDto::postId)
                .collect(Collectors.toSet())
                .stream()
                .limit(requireCount - postIds.size())
                .collect(Collectors.toSet());

        postIds.addAll(extraPostIds);


        return postIds;
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