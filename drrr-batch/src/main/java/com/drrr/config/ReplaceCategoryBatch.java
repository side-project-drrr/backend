package com.drrr.config;

import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.entity.BatchProcessingTechBlog;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ReplaceCategoryBatch {
    public static final String JOB_NAME = "ReplaceCategoryBatchJop";
    public static final String STEP_NAME = "ReplaceCategoryBatchStep";
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;


    @Bean(JOB_NAME)
    Job replaceCategoryJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(replaceCategoryStep())
                .build();
    }

    @Bean(STEP_NAME)
    Step replaceCategoryStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<BatchProcessingTechBlog, BatchProcessingTechBlog>chunk(100, transactionManager)
                .reader(replaceCategoryItemReader())
                .processor(post -> {
                    post.replaceCategories(categoryRepository::findById);
                    return post;
                })
                .writer(new JpaItemWriterBuilder<>()
                        .usePersist(false)
                        .entityManagerFactory(entityManagerFactory)
                        .build()
                )
                .build();
    }

    @Bean
    ItemReader<BatchProcessingTechBlog> replaceCategoryItemReader() {
        return new JpaCursorItemReaderBuilder<BatchProcessingTechBlog>()
                .name("ReplaceCategoryBatchReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                         select P from BatchProcessingTechBlog P
                            left join fetch P.categories PC
                            left join fetch PC.category
                        """)
                .build();
    }
}
