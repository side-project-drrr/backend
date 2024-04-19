package com.drrr.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.drrr.domain.category.domain.CategoryWeights;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.util.ServiceIntegrationTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PostCategoryUtilityServiceTest extends ServiceIntegrationTest {


    @Autowired
    PostCategoryUtilityService postCategoryUtilityService;

    @Test
    void 가중치별로_게시물_할당이_제대로_이루어집니다() {
        List<CategoryWeight> categoryWeights = categoryWeightRepository.saveAll(
                CategoryWeightFixture.createCategoryWeights(
                        memberRepository.save(MemberFixture.createMember()),
                        categoryRepository.saveAll(CategoryFixture.createCategories(3)),
                        List.of(5.0, 3.0, 2.0))
        );

        // 입력 -> 어떤 결과
        List<Category> all = categoryRepository.findAll();

        Map<Long, Integer> actual = postCategoryUtilityService.calculatePostDistribution(
                new CategoryWeights(categoryWeights), 10);

        assertAll(
                () -> assertThat(actual.values()).containsExactlyInAnyOrder(5, 3, 2),
                () -> assertThat(actual.size()).isEqualTo(3)
        );
    }

}