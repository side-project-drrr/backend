package com.drrr.domain.category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.util.ServiceIntegrationTest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CategoryWeightsTest extends ServiceIntegrationTest {
    @Test
    void 가중치별_게시글_할당이_정상적으로_이루어집니다() {
        //given
        Member member = MemberFixture.createMember();
        List<Category> categories = CategoryFixture.createCategories(5);
        categoryRepository.saveAll(categories);
        List<CategoryWeight> categoryWeights = CategoryWeightFixture.createCategoryWeights(member, categories,
                Arrays.asList(4.75, 1.73, 10.23, 8.42, 5.93));
        categoryWeightRepository.saveAll(categoryWeights);
        CategoryWeights weights = new CategoryWeights(categoryWeights);

        //when
        Map<Long, Integer> distribution = weights.calculatePostDistribution(10);

        //then
        assertAll(
                () -> assertThat(
                        distribution.entrySet().stream().map(Map.Entry::getValue).toList())
                        .containsExactlyInAnyOrder(1, 1, 3, 3, 2),
                () -> assertThat(distribution.values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(10),
                () -> assertThat(distribution.size()).isEqualTo(5)
        );
    }
}