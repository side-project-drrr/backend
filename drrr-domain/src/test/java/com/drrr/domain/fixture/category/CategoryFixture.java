package com.drrr.domain.fixture.category;

import com.drrr.domain.category.entity.Category;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class CategoryFixture {
    private final static int count = 20;

    public static List<Category> createCategories() {
        return IntStream.range(0, count).mapToObj(i -> Category.builder()
                .name("name" + i)
                .build()).collect(Collectors.toList());
    }
}
