package com.drrr.config;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class MigrationBatchConfiguration {
    public static final String BATCH_NAME = "migration";
    public static final String JOB_NAME = BATCH_NAME + "Job";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final TechBlogPostRepository techBlogPostRepository;
    private final TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    private final TemporalTechPostTagRepository temporalTechPostTagRepository;
    private final TemporalTechBlogPostRepository temporalTechBlogPostRepository;

    @Bean(name = JOB_NAME)
    public Job migrationJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
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
                .<TemporalTechBlogPost, TemporalTechBlogPost>chunk(100, transactionManager)
                .reader(new JpaCursorItemReaderBuilder<TemporalTechBlogPost>()
                        .name("migrationJbcItemReader")
                        .entityManagerFactory(entityManagerFactory)
                        .queryString("""
                                select T from TemporalTechBlogPost  T
                                where T.techBlogCode = :techBlogCode
                                """)
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
                    if (temporalTechBlogEntity.isRegistrationCompleted()) {
                        return temporalTechBlogEntity;
                    }
                    return null;
                })
                .writer(this::executeMigration)
                .build();
    }

    private void executeMigration(Chunk<? extends TemporalTechBlogPost> temporalTechBlogPosts) {

        final var deleteList = new ArrayList<TemporalTechBlogPost>();
        for (var temporalTechBlogPost : temporalTechBlogPosts) {
            final var techBlogPost = this.techBlogPostRepository.save(TechBlogPost.from(temporalTechBlogPost));
            final var techBlogPostCategories = this.temporalTechPostTagRepository.findByTemporalTechBlogPostId(
                            temporalTechBlogPost.getId())
                    .stream()
                    .map(temporalTechPostTag -> TechBlogPostCategory.from(techBlogPost,
                            temporalTechPostTag))
                    .toList();
            techBlogPostCategoryRepository.saveAll(techBlogPostCategories);

            deleteList.add(temporalTechBlogPost);
        }

        temporalTechBlogPostRepository.deleteAll(deleteList);
    }
}