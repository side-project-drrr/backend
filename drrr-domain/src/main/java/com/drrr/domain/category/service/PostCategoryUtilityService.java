package com.drrr.domain.category.service;

import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.service.RecommendPostService.ExtractedPostCategoryDto;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostCategoryUtilityService {

    /**
     * 하나의 기술블로그에 대한 여러개의 카테고리를 객체로 각각 저장 techBlogPosts
     */
    public Map<Long, Set<Long>> classifyPostWithCategoriesByMap(final List<ExtractedPostCategoryDto> techBlogPosts) {

        //key : 기술블로그 id, value : 카테고리 id
        return techBlogPosts.stream()
                .collect(Collectors.groupingBy(
                        ExtractedPostCategoryDto::postId,
                        LinkedHashMap::new,
                        Collectors.mapping(ExtractedPostCategoryDto::categoryId, Collectors.toSet())
                ));

    }

    /**
     * 카테고리 별로 몇개의 게시물을 추천해줄 건지 계산 return : key - 카테고리_아이디, value - 카테고리별 추천 게시물 수 categoryWeightDtos - 가장 최근 게시물 순으로
     * 정렬되어 있는 상태
     */
    public Map<Long, Integer> calculatePostDistribution(final List<CategoryWeightDto> categoryWeightDtos,
                                                        final int count) {
        final int totalPosts = count;

        // Calculate the total weight
        final double totalWeight = categoryWeightDtos.stream().mapToDouble(CategoryWeightDto::value).sum();

        // key : categoryId, value : 가중치 백분율
        final Map<Long, Double> fractionalPartsMap = new LinkedHashMap<>();
        final Map<Long, Integer> resultMap = new HashMap<>();

        // 비율에 맞게 카테고리별 기술 블로그 할당
        final int remainingPosts = categoryWeightDtos.stream()
                .map(dto -> {
                    double rawPostCount = (dto.value() / totalWeight) * totalPosts;
                    int postCount = (int) rawPostCount;
                    double fractionalPart = rawPostCount - postCount;

                    fractionalPartsMap.put(dto.category().getId(), fractionalPart);
                    resultMap.put(dto.category().getId(), postCount);

                    return postCount;
                })
                .reduce(totalPosts, (a, b) -> a - b);

        //남은 소수점을 고려해서 remainingPosts 만큼 또 다시 할당
        //key : 카테고리Id, value : 가중치
        fractionalPartsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .filter(entry -> resultMap.containsKey(entry.getKey()))
                .limit(remainingPosts)
                .forEach(entry -> resultMap.put(entry.getKey(), resultMap.get(entry.getKey()) + 1));

        return resultMap;
    }
}
