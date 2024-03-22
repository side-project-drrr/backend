package com.drrr.config;


import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.processor.RegisterCategoryItemProcessor;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExtractCategoryBatchConfiguration {

    public static final String JOB_NAME = "parseCategoryJob";
    public static final String STEP_NAME = "parseCategoryStep";
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final EntityManagerFactory entityManagerFactory;
    private final RegisterCategoryItemProcessor registerCategoryItemProcessor;

    @Bean
    Job job() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step step() {
        log.info("start step");
        return new StepBuilder(STEP_NAME, jobRepository)
                .allowStartIfComplete(true)
                .<TemporalTechBlogPost, TemporalTechBlogPost>chunk(1, transactionManager)
                .reader(new JpaPagingItemReaderBuilder<TemporalTechBlogPost>()
                        .name("TemporalTechBlogPostItemReader")
                        .queryString(
                                "select T from TemporalTechBlogPost T where T.registrationCompleted = false and T.techBlogCode=TECHOBLE")
                        .entityManagerFactory(entityManagerFactory)
                        .build())
                .processor(registerCategoryItemProcessor)
                .writer(new JpaItemWriterBuilder<>()
                        .entityManagerFactory(entityManagerFactory)
                        .build())
                .build();
    }


}
