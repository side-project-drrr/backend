package com.drrr.config;


import com.drrr.domain.jpa.entity.BaseEntity;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RemoveIgnoreCategoryBatch {

    public static final String JOB_NAME = "RemoveIgnoreCategoryJob";
    private static final String REMOVE_TEMP_POST_STEP_NAME = "RemoveTempPostIgnoreCategoryStep";
    private static final String REMOVE_POST_STEP_NAME = "RemovePostIgnoreCategoryStep";

    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final TemporalTechPostTagRepository temporalTechPostTagRepository;
    private final TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    private final JobRepository jobRepository;

    @Bean(JOB_NAME)
    Job removeTempPostIgnoreCategoryBatchJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(removePostIgnoreCategoryStep())
                .build();

    }

    @Bean(REMOVE_POST_STEP_NAME)
    Step removePostIgnoreCategoryStep() {
        return new StepBuilder(REMOVE_POST_STEP_NAME, jobRepository)
                .<TechBlogPostCategory, TechBlogPostCategory>chunk(10, transactionManager)
                .reader(techBlogPostJpaCursorItemReader())
                .writer(chunk -> techBlogPostCategoryRepository.deleteAllByIdInBatch(
                        chunk.getItems()
                                .stream()
                                .map(BaseEntity::getId)
                                .toList()))
                .build();
    }

    @Bean(REMOVE_TEMP_POST_STEP_NAME)
    Step removeTempPostIgnoreCategoryStep() {
        return new StepBuilder(REMOVE_TEMP_POST_STEP_NAME, jobRepository)
                .<TemporalTechPostTag, TemporalTechPostTag>chunk(10, transactionManager)
                .reader(temporalTechBlogPostItemReader())
                .writer(chunk -> temporalTechPostTagRepository.deleteAllByIdInBatch(
                        chunk.getItems()
                                .stream()
                                .map(BaseEntity::getId)
                                .toList()
                ))
                .build();
    }

    @Bean("temporalTechBlogPostItemReader")
    @StepScope
    JpaCursorItemReader<TemporalTechPostTag> temporalTechBlogPostItemReader() {
        return new JpaCursorItemReaderBuilder<TemporalTechPostTag>()
                .name("temporalTechBlogPostItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        select Tag from TemporalTechPostTag Tag
                        join Category category
                        on Tag.category = category
                        where category.metaCategoryType = 2
                        """)
                .build();
    }

    @Bean("techBlogPostJapCursorItemReader")
    @StepScope
    JpaCursorItemReader<TechBlogPostCategory> techBlogPostJpaCursorItemReader() {
        return new JpaCursorItemReaderBuilder<TechBlogPostCategory>()
                .name("techBlogPostJpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        select tbpc from TechBlogPostCategory tbpc join Category c
                         on c = tbpc.category
                         where c.metaCategoryType = 2
                        """)
                .build();

    }

}
