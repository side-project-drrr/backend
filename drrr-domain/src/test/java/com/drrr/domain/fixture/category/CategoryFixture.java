package com.drrr.domain.fixture.category;

import com.drrr.domain.category.entity.Category;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CategoryFixture {

    public static List<Category> createCategories(final int count) {
        return IntStream.range(0, count).mapToObj(i -> Category.builder()
                .name("name" + i)
                .build()).collect(Collectors.toList());
    }

    public static Category createCategory() {
        return Category.builder()
                .name("name")
                .build();
    }

}
