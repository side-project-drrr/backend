package com.drrr.domain.category.service;

import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepositoryImpl;
import com.drrr.domain.techblogpost.repository.impl.CustomTechBlogPostRepositoryImpl;
import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
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
    private final PostCategoryUtilityService postDistributionService;

    @Transactional
    public List<Long> recommendPosts(final Long memberId, int count) {
        //오늘 추천은 받았으나 안 읽었던 추천 게시물 다시 가져와서 반환
        List<Long> todayUnreadRecommendPostIds = memberPostLogRepository.findTodayUnreadRecommendPostIds(memberId);

        int requireCount = count;

        if (count == todayUnreadRecommendPostIds.size()) {
            return todayUnreadRecommendPostIds;
        }
        //오늘 추천은 받았으나 안 읽었던 추천 게시물은 유지하고 추가적으로 추천해줘야 하는 게시물 수를 계산
        if (requireCount > todayUnreadRecommendPostIds.size()) {
            requireCount -= todayUnreadRecommendPostIds.size();
        }

        //카테고리_가중치 Mapping Table를 특정 MemberId로 조회
        final List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        if (categoryWeights.isEmpty()) {
            log.error("카테고리 가중치를 찾을 수 없습니다.");
            log.error("memberId -> {}", memberId);
            throw DomainExceptionCode.CATEGORY_WEIGHT_NOT_FOUND.newInstance();
        }
        List<Long> categoryIds = categoryWeights.stream().map(cw -> cw.getCategory().getId()).toList();

        //사용자에게 추천해줄 수 있는 모든 기술블로그 가져오기
        final List<ExtractedPostCategoryDto> extractedPostsCategories = customTechBlogPostCategoryRepository.findPostsByCategoryIdsNotInLog(
                categoryIds, memberId);

        //가져온 모든 기술블로그에 해당하는 카테고리들을 set으로 중복 제거
        Set<Long> containedCategoryIds = extractedPostsCategories.stream()
                .map(ExtractedPostCategoryDto::categoryId)
                .collect(Collectors.toSet());

        //중복제거된 카테고리 set의 entity를 필터링해서 dto 변환
        final List<CategoryWeightDto> categoryWeightDtos = categoryWeights.stream()
                .filter(categoryWeight -> containedCategoryIds.contains(categoryWeight.getCategory().getId()))
                .map(categoryWeight -> CategoryWeightDto.builder()
                        .member(categoryWeight.getMember())
                        .category(categoryWeight.getCategory())
                        .value(categoryWeight.getWeightValue())
                        .preferred(categoryWeight.isPreferred())
                        .build()).toList();

        //추천할 게시물 ids를 카테고리별로 담아서 반환
        //categoryIdToPostCounts -> key : categoryId, value : 할당해야 하는 기술블로그 개수
        final Map<Long, Integer> distributionMap = postDistributionService.calculatePostDistribution(
                categoryWeightDtos, count);

        List<Long> postIds = extractRecommendPostIds(extractedPostsCategories, distributionMap, requireCount,
                new ArrayList<>(), memberId);
        postIds.addAll(todayUnreadRecommendPostIds);
        return postIds;
    }


    /**
     *
     */
    private List<Long> extractRecommendPostIds(
            final List<ExtractedPostCategoryDto> extractedPostsCategories,
            final Map<Long, Integer> categoryIdToPostCounts, final int requireCount,
            final List<Long> postIds, final Long memberId
    ) {
        //이 set에 담긴 카테고리 아이디에 해당하는 게시물을 찾을 거임
        Set<Long> categoryIdSet = extractedPostsCategories.stream()
                .map(ExtractedPostCategoryDto::categoryId)
                .collect(Collectors.toSet());

        List<ExtractedPostCategoryDto> unCheckedPosts = extractedPostsCategories.stream()
                .filter(dto -> {
                    if (requireCount - postIds.size() == 0) {
                        return true;
                    }

                    if (!categoryIdToPostCounts.containsKey(dto.categoryId)) {
                        return true;
                    }
                    int postCount = categoryIdToPostCounts.get(dto.categoryId);

                    if (!postIds.contains(dto.postId) && categoryIdSet.contains(dto.categoryId) && postCount > 0) {
                        categoryIdToPostCounts.put(dto.categoryId, postCount - 1);
                        postIds.add(dto.postId);
                        return false;
                    }
                    return true;
                })
                .toList();

        if (requireCount - postIds.size() == 0) {
            return postIds;
        }

        if (requireCount < unCheckedPosts.size()) {
            postIds.addAll(
                    unCheckedPosts.subList(0, requireCount).stream().map(ExtractedPostCategoryDto::postId).toList());
            return postIds;
        }

        postIds.addAll(unCheckedPosts.stream().map(ExtractedPostCategoryDto::postId).toList());
        return postIds;




/*        //지금 가령 5개 가져와야 할 때 categoryId가 1,2,3,4,5 일때 1에 해당하는 포스트가 아예없으면
        //4개만 가져오는 문제가 발생, 즉 categoryId 1에 대해서 post가 1개 할당해야 할 때 더이상 할당할 수 있는 post가 없는 경우임
        //이런 경우에는 다른 카테고리에서 가져와야 함

        //아직 더 할당해야 하는 수
        int sum = categoryIdToPostCounts.values().stream().mapToInt(Integer::intValue).sum();
        //할당이 안되는 카테고리
        List<Long> remainedCategoryIds = categoryIdToPostCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .toList();
        findAnotherCategoryWeight(remainedCategoryIds, postIds, memberId, extractedPostsCategories, sum);

        return postIds;*/
    }

    //재귀함수
    public void findAnotherCategoryWeight(final List<Long> remainedCategoryIds, final List<Long> postIds,
                                          final Long memberId,
                                          final List<ExtractedPostCategoryDto> extractedPostsCategories,
                                          final int sum) {
        //할당이 안되는 카테고리가 아닌 다른 카테고리 가중치 가져오기
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findCategoryWeightNotInCategoryIds(memberId,
                remainedCategoryIds);

        if (categoryWeights.isEmpty()) {
            return;
        }

        List<CategoryWeightDto> categoryWeightDtos = categoryWeights.stream()
                .map(categoryWeight -> CategoryWeightDto.builder()
                        .member(categoryWeight.getMember())
                        .category(categoryWeight.getCategory())
                        .value(categoryWeight.getWeightValue())
                        .preferred(categoryWeight.isPreferred())
                        .build()).toList();
        Map<Long, Integer> categoryIdToPostCounts = postDistributionService.calculatePostDistribution(
                categoryWeightDtos,
                sum);
        List<Long> postIdsResult = extractRecommendPostIds(extractedPostsCategories, categoryIdToPostCounts, sum,
                postIds, memberId);

        if (postIdsResult.size() != sum) {
            List<Long> remained = categoryIdToPostCounts.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            remained.addAll(remainedCategoryIds);

            findAnotherCategoryWeight(remained, postIds, memberId, extractedPostsCategories, sum);
        }


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