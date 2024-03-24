package com.drrr.config;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.processor.RegisterCategoryItemProcessor;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
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
                .start(step(null))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @JobScope
    Step step(
            @Value("#{jobParameters[techBlogCode]}") Long code
    ) {

        var techBlogCode = TechBlogCode.valueOf(code);

        log.info("create step");
        return new StepBuilder(STEP_NAME, jobRepository)
                .allowStartIfComplete(true)
                // 청크 사이즈가 1인 이유는 임시 기술블로그에서 카테고리 추출하는 시간이 비교적 길기 떄문입니다.
                .<TemporalTechBlogPost, TemporalTechBlogPost>chunk(1, transactionManager)
                .reader(new JpaPagingItemReaderBuilder<TemporalTechBlogPost>()
                        .name("TemporalTechBlogPostItemReader")
                        .queryString("select T from TemporalTechBlogPost T where T.techBlogCode=:code")
                        .parameterValues(Map.of("code", techBlogCode))
                        .entityManagerFactory(entityManagerFactory)
                        .build())
                .writer(registerCategoryItemProcessor)
                .build();
    }


}
