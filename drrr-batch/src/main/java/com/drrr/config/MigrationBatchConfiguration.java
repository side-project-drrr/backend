package com.drrr.config;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "spring.batch.job.name", havingValue = "migrationJob")
public class MigrationBatchConfiguration {
    private static final String BATCH_NAME = "migration";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final TechBlogPostRepository techBlogPostRepository;

    @Bean(name = BATCH_NAME + "Job")
    public Job migrationJob() {
        return new JobBuilder(BATCH_NAME + "Job", jobRepository)
                .start(migrationStep(null, null))
                .build();
    }

    @Bean(name = BATCH_NAME + "Step")
    @JobScope
    public Step migrationStep(
            @Value("#{jobParameters[techBlogCode]}") Long code,
            @Value("#{jobParameters[requestDate]}") String requestDate
    ) {
        final var techBlogCode = TechBlogCode.valueOf(code);
        return new StepBuilder(BATCH_NAME + "Step", jobRepository)
                .<TemporalTechBlogPost, TechBlogPost>chunk(100, transactionManager)
                .reader(new JpaCursorItemReaderBuilder<TemporalTechBlogPost>()
                        .name("migrationJbcItemReader")
                        .entityManagerFactory(entityManagerFactory)
                        .queryString("select T from TemporalTechBlogPost T where  T.techBlogCode = :techBlogCode")
                        .parameterValues(new HashMap<>() {{
                            this.put("techBlogCode", techBlogCode);
                        }})
                        .build())
                .processor((temporalTechBlogEntity) -> {
                    if (this.techBlogPostRepository.existsByTechBlogCodeAndUrlSuffix(
                            temporalTechBlogEntity.getTechBlogCode(),
                            temporalTechBlogEntity.getUrlSuffix())) {
                        return null;
                    }
                    return TechBlogPost.from(temporalTechBlogEntity);
                })
                .writer(new JpaItemWriterBuilder<TechBlogPost>()
                        .usePersist(true)
                        .entityManagerFactory(entityManagerFactory)
                        .build())
                .build();
    }
}