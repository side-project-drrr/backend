package com.example.drrrbatch.baemin.batch;


import com.example.drrrbatch.baemin.entity.TechBlog;
import com.example.drrrbatch.baemin.repository.TechBlogRepository;
import com.example.drrrbatch.baemin.utility.SeleniumUtil;
import com.example.drrrbatch.baemin.utility.WebDriverFactory;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class BatchConfig {
    private final TechBlogRepository techBlogRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final WebDriverFactory webDriverFactory;
    private final SeleniumUtil seleniumUtil;

    private WebCrawlingItemProcessor processor() {
        return new WebCrawlingItemProcessor();
    }

    private WebCrawlingItemReader reader() {
        return new WebCrawlingItemReader(seleniumUtil, webDriverFactory);
    }

    private WebCrawlingItemWriter writer() {
        JpaItemWriter<TechBlog> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return new WebCrawlingItemWriter<>(writer);
    }

    @Bean
    public Step webCrawlingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        // StepBuilder 객체를 직접 생성
        return new StepBuilder("webCrawlingStep", jobRepository).<List<TechBlog>, List<TechBlog>>chunk(10,
                transactionManager).reader(reader()).processor(processor()).writer(writer()).build();
    }

    private WebCrawlingTasklet webCrawlingTasklet() {
        return new WebCrawlingTasklet(techBlogRepository, seleniumUtil, webDriverFactory);
    }

    @Bean
    public Step webCrawlingTaskletStep(JobRepository repository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("webCrawlingTaskletStep", repository).tasklet(webCrawlingTasklet(), transactionManager)
                .build();
    }
    @Bean
    public WebCrawlingDecider webCrawlingDecider() {
        return new WebCrawlingDecider();
    }

    /**
     * decider 쓰는 경우 모든 경우의 수에 대한 on()를 작성해줄 것
     * 아니면 step은 정상적으로 작동하나 최종 Exit Code는 FAILED가 반환됨
     */
    @Bean
    public Job webCrawlingJob(JobRepository repository, JobCompletionNotificationListener listener,
                              Step webCrawlingStep, Step webCrawlingTaskletStep) {
        return new JobBuilder("webCrawlingJob", repository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(webCrawlingTaskletStep)
                .next(webCrawlingDecider())
                .on("COMPLETED")
                .end()
                .on("CONTINUE")
                .to(webCrawlingStep)
                .on("COMPLETED")
                .end()
                .end()
                .build();
    }
}
