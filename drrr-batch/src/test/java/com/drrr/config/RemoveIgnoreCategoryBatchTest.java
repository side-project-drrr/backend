package com.drrr.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.config.util.SpringBatchTestSupport;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostCategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.fixture.post.TemporalTechBlogPostFixture;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;


class RemoveIgnoreCategoryBatchTest extends SpringBatchTestSupport {

    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TemporalTechPostTagRepository temporalTechPostTagRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;

    @Test
    void reader_쿼리_테스트() {
        var post = temporalTechBlogPostRepository.save(TemporalTechBlogPostFixture.createTechBlogPost());
        var temporalTechBlogPost = temporalTechBlogPostRepository.save(
                TemporalTechBlogPostFixture.createTechBlogPost()
        );
        var createdData = CategoryFixture.createIgnoreCategories(10);
        var categories = categoryRepository.saveAll(createdData);

        temporalTechPostTagRepository.saveAll(
                categories.stream()
                        .map(category -> new TemporalTechPostTag(category, temporalTechBlogPost))
                        .toList()
        );

        var previousCount = temporalTechPostTagRepository.count();

        temporalTechPostTagRepository.saveAll(categories.stream()
                .map(category -> new TemporalTechPostTag(category, post))
                .toList());

        this.launchJob(RemoveIgnoreCategoryBatch.JOB_NAME, new JobParameters());
        assertThat(previousCount).isEqualTo(10);
        assertThat(temporalTechPostTagRepository.count()).isEqualTo(0);
    }

    @Test
    void 쿼리_테스트() {
        var post = techBlogPostRepository.save(TechBlogPostFixture.createTechBlogPost());

        techBlogPostCategoryRepository.saveAll(
                TechBlogPostCategoryFixture.createTechBlogPostCategories(post,
                        categoryRepository.saveAll(CategoryFixture.createIgnoreCategories(10))));

        var previousCount = temporalTechPostTagRepository.count();

        this.launchJob(RemoveIgnoreCategoryBatch.JOB_NAME, new JobParameters());

        assertThat(previousCount).isEqualTo(10);
        assertThat(techBlogPostCategoryRepository.count()).isEqualTo(0);
    }

}