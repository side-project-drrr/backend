package com.drrr.domain.category.service;

import com.drrr.domain.category.domain.CategoryWeights;

import java.util.Map;

import lombok.Builder;
import org.springframework.stereotype.Service;

@Service
public class PostCategoryUtilityService {
    /**
     * 카테고리 별로 몇개의 게시물을 추천해줄 건지 계산 return : key - 카테고리_아이디, value - 카테고리별 추천 게시물 수 categoryWeightDtos - 가장 최근 게시물 순으로
     * 정렬되어 있는 상태
     */
    public Map<Long, Integer> calculatePostDistribution(final CategoryWeights categoryWeights, final int total) {
        return categoryWeights.calculatePostDistribution(total);
    }

}
