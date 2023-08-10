package com.example.drrrbatch.batch.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CrawlingBatchConfiguration {
    private static final String BATCH_NAME = "Crawling";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean(name = BATCH_NAME + "job")
    public Job crawlingJob() {
        return new JobBuilder(BATCH_NAME + "Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(crawlingStep(null))
                .build();
    }

    @Bean(name = BATCH_NAME + "step")
    @JobScope
    public Step crawlingStep(
            @Value("#{jobParameters[techBlogCode]}") Long code
    ) {
        log.info("get parameter code: {}", code);
        return new StepBuilder(BATCH_NAME + "Step", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(BATCH_NAME + " run ");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

}
