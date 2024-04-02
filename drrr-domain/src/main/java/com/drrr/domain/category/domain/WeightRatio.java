package com.drrr.domain.category.domain;

import com.drrr.domain.category.entity.CategoryWeight;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightRatio {
    private Long categoryId;
    private double fractional;
    private int count;

    public void increaseDistributionCount() {
        this.count++;
    }

    public static WeightRatio from(CategoryWeight weight, double totalWeight, int count) {
        double rawPostCount = (weight.getWeightValue() / totalWeight) * count;
        int postCount = (int) rawPostCount;
        double fractionalPart = rawPostCount - postCount;

        return WeightRatio.builder()
                .categoryId(weight.getCategory().getId())
                .fractional(fractionalPart)
                .count(postCount)
                .build();
    }
}
