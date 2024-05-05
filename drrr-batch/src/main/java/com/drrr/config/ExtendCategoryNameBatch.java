package com.drrr.config;


import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.entity.BatchProcessingTechBlog;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
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

@Configuration
@RequiredArgsConstructor
public class ExtendCategoryNameBatch {
    public static final String BATCH_NAME = "ExtendCategoryNameBatch";
    public static final String JOB_NAME = BATCH_NAME + "Job";
    public static final String STEP_NAME = BATCH_NAME + "Step";

    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;


    @Bean(JOB_NAME)
    Job extendCategoryJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(extendCategoryStep())
                .build();
    }


    @Bean(STEP_NAME)
    Step extendCategoryStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<BatchProcessingTechBlog, BatchProcessingTechBlog>chunk(100, transactionManager)
                .reader(batchProcessingTechBlogItemReader())
                .processor(extendCategoryNameItemProcessor())
                .writer(extendCategoryNameItemWriter())
                .build();
    }


    @Bean
    ItemReader<BatchProcessingTechBlog> batchProcessingTechBlogItemReader() {
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
    ItemProcessor<BatchProcessingTechBlog, BatchProcessingTechBlog> extendCategoryNameItemProcessor() {
        return post -> {
            post.extendCategories(categoryRepository::findById);
            return post;
        };
    }

    @Bean
    ItemWriter<BatchProcessingTechBlog> extendCategoryNameItemWriter() {
        return new JpaItemWriterBuilder<BatchProcessingTechBlog>()
                .usePersist(false)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}