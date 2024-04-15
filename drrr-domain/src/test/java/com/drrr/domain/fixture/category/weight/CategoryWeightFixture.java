package com.drrr.domain.fixture.category.weight;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CategoryWeightFixture {
    private final static double DEFAULT_WEIGHT_VALUE = 3.0;


    public static List<CategoryWeight> createCategoryWeights(final Member member, final List<Category> categories) {

        final List<CategoryWeight> categoryWeightList = categories.stream()
                .map(category -> CategoryWeight.builder()
                        .member(member)
                        .category(category)
                        .weightValue(DEFAULT_WEIGHT_VALUE)
                        .build()).toList();

        return categoryWeightList;
    }

    public static List<CategoryWeight> createCategoryWeights(final Member member, final List<Category> categories,
                                                             final double weightValue) {

        return categories.stream()
                .map(category -> CategoryWeight.builder()
                        .member(member)
                        .category(category)
                        .weightValue(weightValue)
                        .lastReadAt(LocalDateTime.now())
                        .preferred(false)
                        .build()).toList();
    }

    public static List<CategoryWeight> createCategoryWeights(final Member member, final List<Category> categories,
                                                             final List<Double> weightValue) {

        return IntStream.range(0, categories.size())
                .mapToObj(i -> CategoryWeight.builder()
                        .member(member)
                        .category(categories.get(i))
                        .weightValue(weightValue.get(i))
                        .lastReadAt(LocalDateTime.now())
                        .preferred(false)
                        .build()).toList();
    }

    public static CategoryWeight createCategoryWeight(final Member member, final Category category) {

        return CategoryWeight.builder()
                .member(member)
                .category(category)
                .weightValue(DEFAULT_WEIGHT_VALUE)
                .lastReadAt(LocalDateTime.now())
                .preferred(false)
                .build();

    }

    public static List<CategoryWeight> createCategoryWeights(final List<Member> members, final Category category) {

        return members.stream()
                .map(member -> CategoryWeight.builder()
                        .member(member)
                        .category(category)
                        .weightValue(DEFAULT_WEIGHT_VALUE)
                        .lastReadAt(LocalDateTime.now())
                        .preferred(false)
                        .build()).toList();

    }

    public static List<CategoryWeight> createCategoryWeights(final List<Member> members,
                                                             final List<Category> categories) {
        List<CategoryWeight> categoryWeightResults = new ArrayList<>();
        members.forEach(member -> {
            List<CategoryWeight> categoryWeights = categories.stream()
                    .map(category -> CategoryWeight.builder()
                            .member(member)
                            .category(category)
                            .weightValue(DEFAULT_WEIGHT_VALUE)
                            .lastReadAt(LocalDateTime.now())
                            .preferred(false)
                            .build()).toList();

            categoryWeightResults.addAll(categoryWeights);
        });
        return categoryWeightResults;

    }

    public static CategoryWeight createCategoryWeight(final Member member, final Category category,
                                                      final double weightValue, final boolean preferred) {

        return CategoryWeight.builder()
                .member(member)
                .category(category)
                .weightValue(weightValue)
                .lastReadAt(LocalDateTime.now())
                .preferred(preferred)
                .build();

    }


}
