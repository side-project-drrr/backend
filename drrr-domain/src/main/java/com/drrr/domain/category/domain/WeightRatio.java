package com.drrr.domain.category.domain;

import com.drrr.domain.category.entity.CategoryWeight;

public record WeightRatio(Long id, double fractional, int count)  {

    public WeightRatio increase(){
        return new WeightRatio(id, fractional, count + 1);
    }

    public static WeightRatio from(CategoryWeight weight, double totalWeight, int count){
        double rawPostCount = (weight.getWeightValue() / totalWeight ) * count;
        int postCount = (int) rawPostCount;
        double fractionalPart = rawPostCount - postCount;

        return new WeightRatio(
                weight.getCategory().getId(),
                fractionalPart,
                postCount
        );
    }
}
