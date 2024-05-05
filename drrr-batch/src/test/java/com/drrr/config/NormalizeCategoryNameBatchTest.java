package com.drrr.config;

import com.drrr.config.util.SpringBatchTestSupport;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class NormalizeCategoryNameBatchTest extends SpringBatchTestSupport {

    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    void 카테고리_재처리_기능_테스트() {

        var category = categoryRepository.save(new Category("Java, Spring, Node js"));
        var post = techBlogPostRepository.save(TechBlogPostFixture.createTechBlogPost());
        techBlogPostCategoryRepository.save(TechBlogPostCategory.builder()
                .category(category)
                .post(post)
                .build()
        );

        this.launchJob(NormalizeCategoryNameBatch.JOB_NAME);

        Assertions.assertThat(techBlogPostCategoryRepository.count()).isEqualTo(3);
        Assertions.assertThat(categoryRepository.count()).isEqualTo(4);

    }

}