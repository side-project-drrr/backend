package com.drrr.domain.category.domain;

import com.drrr.domain.category.service.RecommendPostService.ExtractedPostCategoryDto;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CategoryPostDistribution {
    private Map<Long, Integer> categoryIdToPostCounts;
    private Set<Long> postIds;

    public Set<Long> extractRecommendPostIds(final List<ExtractedPostCategoryDto> extractedPostsCategories, final int requireCount) {
        extractedPostsCategories.forEach(dto -> {
            if (hasPostToDistribute(dto.categoryId()) && postIds.add(dto.postId())) {
                categoryIdToPostCounts.put(dto.categoryId(), categoryIdToPostCounts.get(dto.categoryId()) - 1);
            }
        });


        if(postIds.size() == requireCount){
            return postIds;
        }

        extractedPostsCategories.forEach(dto -> {
                    if(postIds.size() < requireCount){
                        postIds.add(dto.postId());
                    }
                });


        return postIds;
    }

    private boolean hasPostToDistribute(final Long categoryId){
        return categoryIdToPostCounts.containsKey(categoryId) && categoryIdToPostCounts.get(categoryId) > 0;
    }
}
