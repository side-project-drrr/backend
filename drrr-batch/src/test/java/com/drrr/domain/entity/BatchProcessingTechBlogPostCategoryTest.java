package com.drrr.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.domain.category.entity.Category;
import org.junit.jupiter.api.Test;

class BatchProcessingTechBlogPostCategoryTest {


    @Test
    void 정규화되지_않은_문자열_여부_테스트() {
        var postCategory = new BatchProcessingTechBlogPostCategory(null, new Category("자바, Node js"));

        assertThat(postCategory.isUnNormalizedCategoryName()).isTrue();


    }

}