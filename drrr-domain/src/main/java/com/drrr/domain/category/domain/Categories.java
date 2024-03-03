package com.drrr.domain.category.domain;

import com.drrr.core.recommandation.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Categories(List<Category> categories) {
    public Categories {
        if (categories.isEmpty()) {
            log.error(
                    "InitializeWeightService Class initializeCategoryWeight(final Long memberId, final List<Long> categories) Method RuntimeException Error");
            throw new RuntimeException(
                    "InitializeWeightService.initializeCategoryWeight() - Cannot find such categories");
        }
    }

    public List<CategoryWeight> toCategoryWeights(Member member) {
        return categories.stream()
                .map(category -> CategoryWeight.builder()
                        .member(member)
                        .category(category)
                        .weightValue(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                        .lastReadAt(LocalDateTime.now())
                        .preferred(true)
                        .build()).toList();
    }

}


