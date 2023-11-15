package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.PostConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepositoryImpl;
import com.drrr.domain.techblogpost.repository.impl.CustomTechBlogPostRepositoryImpl;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecommendPostService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final CustomTechBlogPostRepositoryImpl customTechBlogPostRepository;

    private final TechBlogPostRepository techBlogPostRepository;
    private final LogUpdateService logUpdateService;
    private final CustomTechBlogPostCategoryRepositoryImpl customTechBlogPostCategoryRepository;
    private final PostCategoryUtilityService postDistributionService;

    private EntityManager em;

    @Transactional
    public List<TechBlogPost> recommendPosts(final Long memberId) {
        int remainingPostCount = 0;
        //카테고리_가중치 Mapping Table를 특정 MemberId로 조회
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        if(categoryWeights.isEmpty()) {
            throw new RuntimeException(
                    "RecommendPostService.recommendPosts() - Cannot find such element -> memberId : " + memberId);
        }

        //entity -> dto 변환
        List<CategoryWeightDto> categoryWeightDtos = categoryWeights.stream()
                .map(categoryWeight -> CategoryWeightDto.builder()
                        .member(categoryWeight.getMember())
                        .category(categoryWeight.getCategory())
                        .value(categoryWeight.getValue())
                        .preferred(categoryWeight.isPreferred())
                        .build()).toList();

        //사용자에게 추천해줄 수 있는 모든 게시물 가져오기
        List<ExtractedPostCategoryDto> techBlogPosts = customTechBlogPostCategoryRepository.getFilteredPost(
                categoryWeightDtos, memberId);

        //게시물에 대해 카테고리별로 정리
        Map<Long, Set<Long>> classifiedPostsDto = postDistributionService.classifyPostWithCategoriesByMap(
                techBlogPosts);
        //추천할 게시물 ids를 카테고리별로 담아서 반환
        //postsPerCategoryMap -> key : categoryId, value : 할당해야 하는 게시물 개수
        Map<Long, Integer> postsPerCategoryMap = postDistributionService.calculatePostDistribution(categoryWeightDtos);

        //카테고리별로 할당된 개수만큼 게시물 추천해서 id 값 담아놓은 리스트
        //postIds - 할당된 게시물 id 리스트
        List<Long> postIds = extractRecommendPostIds(classifiedPostsDto, postsPerCategoryMap);

        remainingPostCount = PostConstants.RECOMMEND_POSTS_COUNT.getValue() - postIds.size();

        //추천해줄 게시물이 더이상 없는 경우
        if (remainingPostCount > 0) {
            postIds.addAll(customTechBlogPostRepository.recommendRemain(memberId, remainingPostCount));
        }

        //post ids에 해당하는 post 객체들 찾기
        List<TechBlogPost> posts = techBlogPostRepository.findAllById(postIds);

        logUpdateService.updateMemberPostRecommendLog(memberId, posts);

        return posts;
    }

    /**
     * 카테고리별로 추천해줄 게시물들을 추출 classifiedPostsDto - 가장 최근 게시물 순으로 정렬되어 있는 상태 postsPerCategoryMap -> key : categoryId, value
     * : 할당해야 하는 게시물 개수 classifiedPostsDto -> key : postId, value : post에 속한 categoryIds
     */
    private List<Long> extractRecommendPostIds(final Map<Long, Set<Long>> classifiedPostsDto,
                                               final Map<Long, Integer> postsPerCategoryMap) {
        return classifiedPostsDto.keySet()
                .stream()
                .filter(key -> {
                    //특정 post의 카테고리 Set
                    Set<Long> categorySet = classifiedPostsDto.get(key);
                    //추천해줘야할 카테고리 중 하나를 filtering
                    final Optional<Entry<Long, Integer>> categoryCountEntry = postsPerCategoryMap.entrySet()
                            .stream()
                            .filter(longIntegerEntry -> {
                                //특정 카테고리에 할당해야 할 post 개수
                                Integer count = longIntegerEntry.getValue();
                                return count != null && count > 0 && categorySet.contains(longIntegerEntry.getKey());
                            }).findFirst();

                    categoryCountEntry.ifPresent(longIntegerEntry -> {
                        postsPerCategoryMap.put(longIntegerEntry.getKey(), longIntegerEntry.getValue() - 1);
                    });
                    return categoryCountEntry.isPresent();
                })
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