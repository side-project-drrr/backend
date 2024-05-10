package com.drrr.config;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.entity.BatchProcessingTechBlog;
import com.drrr.domain.entity.BatchProcessingTechBlogPostCategory;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class NormalizeCategoryNameBatch {
    public static final String BATCH_NAME = "NormalizeCategoryName";
    public static final String JOB_NAME = BATCH_NAME + "Job";
    public static final String STEP_NAME = BATCH_NAME + "Step";


    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;


    @Bean(JOB_NAME)
    Job normalizeCategoryNameJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(normalizeCategoryNameStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean(STEP_NAME)
    Step normalizeCategoryNameStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<BatchProcessingTechBlog, BatchProcessingTechBlog>chunk(100, transactionManager)
                .reader(normalizeCategoryNameItemReader())
                .processor(normalizeCategoryNameItemProcessor())
                .writer(normalizeCategoryNameItemWriter())
                .build();

    }

    @Bean
    ItemReader<BatchProcessingTechBlog> normalizeCategoryNameItemReader() {
        return new JpaCursorItemReaderBuilder<BatchProcessingTechBlog>()
                .name(BATCH_NAME + "Reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        select P from BatchProcessingTechBlog P
                            left join fetch P.categories PC
                            left join fetch PC.category
                        """)
                .build();

    }

    @Bean
    ItemProcessor<BatchProcessingTechBlog, BatchProcessingTechBlog> normalizeCategoryNameItemProcessor() {
        return (post) -> {
            final var normalizedCategory = post.executeNormalize();

            if (normalizedCategory.isEmpty()) {
                return post;
            }

            log.info("post id {}, names:{}", post.getId(), post.getCategories()
                    .stream()
                    .map(BatchProcessingTechBlogPostCategory::getCategoryName)
                    .toList()
            );

            final var newCategories = normalizedCategory.stream()
                    .map(this::findCategory)
                    .toList();

            post.register(categoryRepository.saveAll(newCategories));

            return post;
        };
    }


    private Category findCategory(Category category) {
        return categoryRepository.findByName(category.getName())
                .orElseGet(() -> categoryRepository.save(category));
    }


    @Bean
    ItemWriter<BatchProcessingTechBlog> normalizeCategoryNameItemWriter() {
        return new JpaItemWriterBuilder<BatchProcessingTechBlog>()
                .usePersist(false)
                .entityManagerFactory(entityManagerFactory)
                .build();

    }
}
