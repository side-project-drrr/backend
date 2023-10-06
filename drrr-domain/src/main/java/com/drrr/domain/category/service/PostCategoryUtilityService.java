package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.PostConstants;
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
     * 하나의 게시물에 대한 여러개의 카테고리를 객체로 각각 저장 techBlogPosts
     */
    public Map<Long, Set<Long>> classifyPostWithCategoriesByMap(List<ExtractedPostCategoryDto> techBlogPosts) {

        Map<Long, Set<Long>> classifiedPostsMap = techBlogPosts.stream()
                .collect(Collectors.groupingBy(
                        ExtractedPostCategoryDto::postId,
                        LinkedHashMap::new,
                        Collectors.mapping(ExtractedPostCategoryDto::categoryId, Collectors.toSet())
                ));
        return classifiedPostsMap;

    }
    /**
     * 카테고리 별로 몇개의 게시물을 추천해줄 건지 계산 return : key - 카테고리_아이디, value - 카테고리별 추천 게시물 수 categoryWeightDtos - 가장 최근 게시물 순으로
     * 정렬되어 있는 상태
     */
    public Map<Long, Integer> calculatePostDistribution(List<CategoryWeightDto> categoryWeightDtos) {
        int totalPosts = PostConstants.RECOMMEND_POSTS_COUNT.getValue();

        // Calculate the total weight
        double totalWeight = categoryWeightDtos.stream().mapToDouble(CategoryWeightDto::value).sum();

        // key : categoryId, value : 가중치 백분율
        Map<Long, Double> fractionalPartsMap = new LinkedHashMap<>();
        Map<Long, Integer> resultMap = new HashMap<>();

        // 카테고리별 게시물 개수
        int remainingPosts = categoryWeightDtos.stream()
                .map(dto -> {
                    double rawPostCount = (dto.value() / totalWeight) * totalPosts;
                    int postCount = (int) rawPostCount;
                    double fractionalPart = rawPostCount - postCount;

                    fractionalPartsMap.put(dto.category().getId(), fractionalPart);
                    resultMap.put(dto.category().getId(), postCount);

                    return postCount;
                })
                .reduce(totalPosts, (a, b) -> a - b);

        //가중치 비율이 제일 높은 순
        //key : 카테고리Id, value : 가중치
        fractionalPartsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .filter(entry -> resultMap.containsKey(entry.getKey()))
                .limit(remainingPosts)
                .forEach(entry -> resultMap.put(entry.getKey(), resultMap.get(entry.getKey()) + 1));

        return resultMap;
    }
}
