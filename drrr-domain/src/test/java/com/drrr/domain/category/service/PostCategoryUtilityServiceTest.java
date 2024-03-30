package com.drrr.domain.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.drrr.domain.category.domain.CategoryWeights;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.ServiceIntegrationTest;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PostCategoryUtilityServiceTest extends ServiceIntegrationTest {


    @Autowired
    PostCategoryUtilityService postCategoryUtilityService;
    @Test
    void 가중치별로_게시물_할당이_제대로_이루어집니다(){
        List<CategoryWeight> categoryWeights = categoryWeightRepository.saveAll(CategoryWeightFixture.createCategoryWeights(
                memberRepository.save(MemberFixture.createMember()),
                categoryRepository.saveAll(CategoryFixture.createCategories(3)),
                List.of(5.0,3.0,2.0))
        );

        // 입력 -> 어떤 결과
        List<Category> all = categoryRepository.findAll();
        for (Category category : all) {
            System.out.println("category = " + category.getId());
        }

        Map<Long, Integer> actual = postCategoryUtilityService.calculatePostDistribution(new CategoryWeights(categoryWeights), 10);
        for (Long aLong : actual.keySet()) {
            System.out.println("key = " + aLong);
            System.out.println("value = " + actual.get(aLong));
        }

        assertAll(
                () -> assertThat(actual.values()).containsExactlyInAnyOrder(5, 3, 2),
                () -> assertThat(actual.size()).isEqualTo(3)
        );
    }

}