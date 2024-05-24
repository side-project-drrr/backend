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


class ExtendCategoryNameBatchTest extends SpringBatchTestSupport {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;


    @Test
    void 카테고리_확장_기능_테스트() {
        var category1 = categoryRepository.save(new Category("Java"));
        var category2 = new Category("Java Api");
        category2.changeExtendType(category1.getId());
        categoryRepository.save(category2);

        var post = techBlogPostRepository.save(TechBlogPostFixture.createTechBlogPost());

        techBlogPostCategoryRepository.save(TechBlogPostCategory.builder()
                .category(category2)
                .post(post)
                .build()
        );

        this.launchJob(ExtendCategoryNameBatch.JOB_NAME);

        Assertions.assertThat(techBlogPostCategoryRepository.count()).isEqualTo(2);
        Assertions.assertThat(categoryRepository.count()).isEqualTo(2);
    }


    @Test
    void 카테고리_확장시_기존_태그와_중복되면_추가되지_않는다() {
        var category1 = categoryRepository.save(new Category("Java"));
        var category2 = new Category("Java Api");
        category2.changeExtendType(category1.getId());
        categoryRepository.save(category2);

        var post = techBlogPostRepository.save(TechBlogPostFixture.createTechBlogPost());

        techBlogPostCategoryRepository.save(TechBlogPostCategory.builder()
                .category(category1)
                .post(post)
                .build()
        );
        techBlogPostCategoryRepository.save(TechBlogPostCategory.builder()
                .category(category2)
                .post(post)
                .build()
        );

        this.launchJob(ExtendCategoryNameBatch.JOB_NAME);

        Assertions.assertThat(techBlogPostCategoryRepository.count()).isEqualTo(2);
        Assertions.assertThat(categoryRepository.count()).isEqualTo(2);
    }
}