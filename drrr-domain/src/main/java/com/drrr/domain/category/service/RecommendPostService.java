package com.drrr.domain.category.service;

import com.drrr.core.exception.category.CategoryExceptionCode;
import com.drrr.core.recommandation.constant.constant.PostConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepositoryImpl;
import com.drrr.domain.techblogpost.repository.impl.CustomTechBlogPostRepositoryImpl;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
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
    private final PostCategoryUtilityService postDistributionService;

    @Transactional
    public List<Long> recommendPosts(final Long memberId) {
        //추천해 줄 기술블로그가 없는 경우 추가적으로 추천해줘야 하는 게시물 수, 초기값 0
        int remainingPostCount = 0;

        //카테고리_가중치 Mapping Table를 특정 MemberId로 조회
        final List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        if (categoryWeights.isEmpty()) {
            log.error("카테고리 가중치를 찾을 수 없습니다.");
            log.error("memberId -> " + memberId);
            throw CategoryExceptionCode.CATEGORY_WEIGHT_NOT_FOUND.newInstance();
        }

        //entity -> dto 변환
        final List<CategoryWeightDto> categoryWeightDtos = categoryWeights.stream()
                .map(categoryWeight -> CategoryWeightDto.builder()
                        .member(categoryWeight.getMember())
                        .category(categoryWeight.getCategory())
                        .value(categoryWeight.getValue())
                        .preferred(categoryWeight.isPreferred())
                        .build()).toList();

        //사용자에게 추천해줄 수 있는 모든 기술블로그 가져오기
        final List<ExtractedPostCategoryDto> techBlogPosts = customTechBlogPostCategoryRepository.getFilteredPost(
                categoryWeightDtos, memberId);

        //기술블로그에 대해 카테고리별로 정리
        final Map<Long, Set<Long>> classifiedPostsDto = postDistributionService.classifyPostWithCategoriesByMap(
                techBlogPosts);
        //추천할 게시물 ids를 카테고리별로 담아서 반환
        //postsPerCategoryMap -> key : categoryId, value : 할당해야 하는 기술블로그 개수
        final Map<Long, Integer> postsPerCategoryMap = postDistributionService.calculatePostDistribution(
                categoryWeightDtos);

        //카테고리별로 할당된 개수만큼 게시물 추천해서 id 값 담아놓은 리스트
        //postIds - 할당된 기술블로그 id 리스트
        final List<Long> postIds = extractRecommendPostIds(classifiedPostsDto, postsPerCategoryMap);

        remainingPostCount = PostConstants.RECOMMEND_POSTS_COUNT.getValue() - postIds.size();

        //추천해줄 기술블로그가 더이상 없는 경우
        if (remainingPostCount > 0) {
            //사용자가 읽은 적이 없는 가장 최근에 작성된 기술블로그들을 추천
            postIds.addAll(customTechBlogPostRepository.recommendRemain(memberId, remainingPostCount));
        }

        return postIds;
    }

    /**
     * 카테고리별로 추천해줄 게시물들을 추출 postCategoriesMapDto - 가장 최근 게시물 순으로 정렬되어 있는 상태 categoriesPostMap -> key : categoryId, value
     * : 카테고리별 추천해야 할 post 개수 : 할당해야 하는 게시물 개수 postCategoriesMapDto -> key : postId, value : post에 속한 categoryIds
     */
    private List<Long> extractRecommendPostIds(final Map<Long, Set<Long>> postCategoriesMapDto,
                                               final Map<Long, Integer> categoryPostsMap) {
        return postCategoriesMapDto.keySet()
                .stream()
                .filter(key -> {
                    //특정 post의 카테고리 Set
                    final Set<Long> categorySet = postCategoriesMapDto.get(key);
                    //추천해줘야할 카테고리 중 하나씩 하나씩 filtering
                    final Optional<Entry<Long, Integer>> categoryCountEntry = categoryPostsMap.entrySet()
                            .stream()
                            .filter(longIntegerEntry -> {
                                //특정 카테고리에 할당해야 할 post 개수
                                final Integer count = longIntegerEntry.getValue();
                                //post에 해당하는 카테고리 중 추천해야줘야할 카테고리가 존재하는 경우 true
                                return count != null && count > 0 && categorySet.contains(longIntegerEntry.getKey());
                            }).findFirst();

                    categoryCountEntry.ifPresent(longIntegerEntry -> {
                        //추천할 기술블로그 하나 추출한 후 해당 key 값에 해당하는 카테고리에 대해 value 값(추천해줘야 하는 개수) 1씩 감소
                        categoryPostsMap.put(longIntegerEntry.getKey(), longIntegerEntry.getValue() - 1);
                    });
                    return categoryCountEntry.isPresent();
                })
                //RECOMMEND_POSTS_COUNT에 정의된 값만큼의 기술블로그를 추천함
                .limit(PostConstants.RECOMMEND_POSTS_COUNT.getValue())
                .toList();
    }


    @Builder
    public record ExtractedPostCategoryDto(
            Long postId,
            Long categoryId
    ) {
    }
}