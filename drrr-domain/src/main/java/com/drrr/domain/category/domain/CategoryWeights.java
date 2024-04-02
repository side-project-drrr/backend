package com.drrr.domain.category.domain;

import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.exception.DomainExceptionCode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record CategoryWeights(List<CategoryWeight> weights) {

    public CategoryWeights {
        if (weights.isEmpty()) {
            log.error("카테고리 가중치를 찾을 수 없습니다.");
            throw DomainExceptionCode.CATEGORY_WEIGHT_NOT_FOUND.newInstance();
        }
    }

    public double calculateTotalWeight() {
        return weights.stream()
                .mapToDouble(CategoryWeight::getWeightValue)
                .sum();
    }

    public Map<Long, Integer> calculatePostDistribution(int count) {
        final double totalWeight = this.calculateTotalWeight();
        List<WeightRatio> weightRatios = weights.stream()
                .map(weight -> WeightRatio.from(weight, totalWeight, count))
                .toList();

        int remainPosts = count - weightRatios.stream().mapToInt(w -> w.getCount()).sum();

        weightRatios.stream()
                .sorted((w1, w2) -> Double.compare(w2.getFractional(), w1.getFractional()))
                .limit(remainPosts)
                .forEach(WeightRatio::increaseDistributionCount);

        return weightRatios.stream()
                .filter(w -> w.getCount() > 0)
                .collect(Collectors.toMap(WeightRatio::getCategoryId, WeightRatio::getCount));
    }

    public List<Long> extractCategoryIds() {
        return weights.stream().map(cw -> cw.getCategory().getId()).toList();
    }
}
