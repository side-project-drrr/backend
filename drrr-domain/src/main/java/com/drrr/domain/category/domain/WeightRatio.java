package com.drrr.domain.category.domain;

import com.drrr.domain.category.entity.CategoryWeight;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeightRatio {
    private final Long categoryId;

    private final double fractional;
    private int count;

    public WeightRatio(final Long categoryId, final double fractional, int count) {
        this.categoryId = categoryId;
        this.fractional = fractional;
        this.count = count;
    }

    public void increaseDistributionCount() {
        this.count++;
    }

    public static WeightRatio from(CategoryWeight weight, double totalWeight, int count) {
        double rawPostCount = (weight.getWeightValue() / totalWeight) * count;
        int postCount = (int) rawPostCount;
        double fractionalPart = rawPostCount - postCount;

        return new WeightRatio(weight.getCategory().getId(), fractionalPart, postCount);
    }
}
