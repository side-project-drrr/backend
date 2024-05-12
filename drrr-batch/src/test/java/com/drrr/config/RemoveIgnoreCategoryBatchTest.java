package com.drrr.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.config.util.SpringBatchTestSupport;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostCategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;


class RemoveIgnoreCategoryBatchTest extends SpringBatchTestSupport {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;


    @Test
    void 쿼리_테스트() {
        var post = techBlogPostRepository.save(TechBlogPostFixture.createTechBlogPost());

        techBlogPostCategoryRepository.saveAll(
                TechBlogPostCategoryFixture.createTechBlogPostCategories(post,
                        categoryRepository.saveAll(CategoryFixture.createIgnoreCategories(10))));

        this.launchJob(RemoveIgnoreCategoryBatch.JOB_NAME, new JobParameters());

        assertThat(techBlogPostCategoryRepository.count()).isEqualTo(0);
    }

}