package com.drrr.domain.fixture.category.weight;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.member.MemberFixture;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.util.ServiceIntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class CategoryWeightFixture extends ServiceIntegrationTest {
    private final static int CATEGORY_WEIGHT_COUNT = 3;
    @Autowired
    private static CategoryRepository categoryRepository;

    public static List<CategoryWeight> createCategoryWeights() {
        List<Member> members = MemberFixture.createMembers();
        List<Category> categories = CategoryFixture.createCategories();
        List<CategoryWeight> categoryWeights = new ArrayList<>();
        List<Category> cats = categoryRepository.findAll();

        members.forEach(member -> {
            List<CategoryWeight> categoryWeightList = IntStream.range(0, CATEGORY_WEIGHT_COUNT).mapToObj(j -> {

                return CategoryWeight.builder()
                        .member(member)
                        .category(cats.get(j))
                        .weightValue(j)
                        .build();
            }).toList();
            categoryWeights.addAll(categoryWeightList);
        });
        return categoryWeights;
    }
}
