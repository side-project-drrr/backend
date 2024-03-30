package com.drrr.domain.category.service;

import com.drrr.domain.category.domain.CategoryWeights;

import com.drrr.domain.category.entity.CategoryWeight;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import org.springframework.stereotype.Service;

@Service
public class PostCategoryUtilityService {
    /**
     * 카테고리 별로 몇개의 게시물을 추천해줄 건지 계산 return : key - 카테고리_아이디, value - 카테고리별 추천 게시물 수 categoryWeightDtos - 가장 최근 게시물 순으로
     * 정렬되어 있는 상태
     */
    public Map<Long, Integer> calculatePostDistribution(final CategoryWeights categoryWeights1, final int total) {
        return categoryWeights1.calculatePostDistribution(total);
    }

    @Builder
    record CategoryCalculation(
        double rawPostCount,
        int postCount,
        double fractionalPart,
        long categoryId
    ){

    }
    @Builder
    record CategoryDistribution(
        Map<Long, Double> fractionalPartsMap, // key : 카테고리 id, value : 비율
        Map<Long, Integer> resultMap   //key : 카테고리 id, value : 할당된 게시물 수
    ){
    }

    // <가중치,비율> 가


}
