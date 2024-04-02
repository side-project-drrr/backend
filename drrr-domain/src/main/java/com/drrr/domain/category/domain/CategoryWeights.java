package com.drrr.domain.category.domain;

import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.exception.DomainExceptionCode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record CategoryWeights(List<CategoryWeight> weights){

    public CategoryWeights {
        if (weights.isEmpty()) {
            log.error("카테고리 가중치를 찾을 수 없습니다.");
            throw DomainExceptionCode.CATEGORY_WEIGHT_NOT_FOUND.newInstance();
        }
    }
    public double calculateTotalWeight(){
        return weights.stream()
                .mapToDouble(CategoryWeight::getWeightValue)
                .sum();
    }

    //별도의 파일로 분리하기

    public Map<Long, Integer> calculatePostDistribution(int count){
        final double totalWeight = this.calculateTotalWeight();
        List<WeightRatio> weightRatios = weights.stream()
                .map(weight -> WeightRatio.from(weight, totalWeight, count))
                .toList();

        int remainPosts = count - weightRatios.stream().mapToInt(WeightRatio::count).sum();

        weightRatios.stream()
                .sorted((w1, w2) -> {
                    if(w2.fractional() == w1.fractional()) {
                        return 0;
                    }

                    return w2.fractional() > w1.fractional() ? 1 : -1;
                })
                .limit(remainPosts)
                .forEach(WeightRatio::increase);

        return weightRatios.stream()
                .filter(w -> w.count() > 0)
                .collect(Collectors.toMap(WeightRatio::id, WeightRatio::count));
    }

    public List<Long> extractCategoryIds(){
        return weights.stream().map(cw -> cw.getCategory().getId()).toList();
    }
}
