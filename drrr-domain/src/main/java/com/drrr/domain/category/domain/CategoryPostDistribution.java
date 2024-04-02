package com.drrr.domain.category.domain;

import com.drrr.domain.category.dto.ExtractedPostCategoryDto;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public record CategoryPostDistribution(
        Map<Long, Integer> categoryIdToPostCounts,
        Set<Long> postIds
) {
    public CategoryPostDistribution(Map<Long, Integer> categoryIdToPostCounts) {
        this(categoryIdToPostCounts, new HashSet<>());
    }

    public Set<Long> extractRecommendPostIds(final List<ExtractedPostCategoryDto> extractedPostsCategories,
                                             final int requireCount) {
        extractedPostsCategories.forEach(dto -> {
            if (hasPostToDistribute(dto.categoryId()) && postIds.add(dto.postId())) {
                categoryIdToPostCounts.put(dto.categoryId(), categoryIdToPostCounts.get(dto.categoryId()) - 1);
            }
        });

        if (postIds.size() == requireCount) {
            return postIds;
        }

        extractedPostsCategories.forEach(dto -> {
            if (postIds.size() < requireCount) {
                postIds.add(dto.postId());
            }
        });

        return postIds;
    }

    private boolean hasPostToDistribute(final Long categoryId) {
        return categoryIdToPostCounts.containsKey(categoryId) && categoryIdToPostCounts.get(categoryId) > 0;
    }
}
