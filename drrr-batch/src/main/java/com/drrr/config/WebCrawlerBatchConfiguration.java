package com.drrr.config;

import com.drrr.core.ProxyItemReader;
import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.reader.CrawlerItemReaderFactory;
import com.drrr.repository.CrawledTechBlogPostRepository;
import com.drrr.repository.CrawledTechBlogPostRepository.Key;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebCrawlerBatchConfiguration {
    public static final String BATCH_NAME = "WebCrawlerBatch";

    public static final String JOB_NAME = BATCH_NAME + "Job";
    public static final String CRAWLING_STEP_NAME = "CrawlingStep";
    public static final String TECH_BLOG_PROCESS_STEP_NAME = "TechBlogProcessStep";
    public static final String TEMP_TECH_BLOG_PROCESS_STEP_NAME = "Temp" + TECH_BLOG_PROCESS_STEP_NAME;
    public static final String CRAWLER_DATA_SAVE_STEP_NAME = "CrawlerDataSaveStep";

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final CrawledTechBlogPostRepository crawledTechBlogPostRepository;
    private final TechBlogPostRepository techBlogPostRepository;
    private final TemporalTechBlogPostRepository temporalTechBlogPostRepository;
    private final CrawlerItemReaderFactory crawlerItemReaderFactory;
    private final EntityManagerFactory entityManagerFactory;

    /**
     * <p>
     * 크롤링 배치의 경우 다음 순서에 따라 처리됩니다.
     * <br>- 크롤링
     * <br>- 기술 블로그 검증
     * <br>- 임시 기술 블로그 검증
     * <br>- 저장
     * </P
     */
    @Bean(JOB_NAME)
    public Job webCrawleJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(crawlingStep(null))
                .next(techBlogProcessStep(null))
                .next(tempTechBlogProcessStep(null))
                .next(crawlingDataSaveStep())
                .build();
    }

    @Bean(CRAWLING_STEP_NAME)
    @JobScope
    public Step crawlingStep(
            @Value("#{jobParameters[techBlogCode]}") Long code
    ) {
        final var proxyItemReader = new ProxyItemReader<>(() -> crawlerItemReaderFactory.createItemReader(TechBlogCode.valueOf(code)));
        return new StepBuilder(CRAWLING_STEP_NAME, jobRepository)
                .<ExternalBlogPosts, ExternalBlogPosts>chunk(10, transactionManager)
                .reader(proxyItemReader)
                .writer(chunk -> chunk.getItems().forEach(crawledTechBlogPostRepository::insertAll))
                .build();
    }

    @Bean(TECH_BLOG_PROCESS_STEP_NAME)
    @JobScope
    public Step techBlogProcessStep(
            @Value("#{jobParameters[techBlogCode]}") Long code
    ) {
        return new StepBuilder(TECH_BLOG_PROCESS_STEP_NAME, jobRepository)
                .<TechBlogPost, TechBlogPost>chunk(10, transactionManager)
                .reader(new JpaCursorItemReaderBuilder<TechBlogPost>()
                        .name("TechBlogReader")
                        .queryString("SELECT T FROM TechBlogPost T where T.techBlogCode = :code")
                        .parameterValues(new HashMap<>() {{
                            this.put("code", TechBlogCode.valueOf(code));
                        }})
                        .entityManagerFactory(entityManagerFactory)
                        .build())
                .writer((chunk -> chunk.getItems().forEach(techBlogPost -> {
                    final Key key = new Key(techBlogPost.getUrlSuffix(), techBlogPost.getTechBlogCode());
                    crawledTechBlogPostRepository.ifPresentOrElse(key,
                            () -> crawledTechBlogPostRepository.remove(key),
                            () -> techBlogPostRepository.delete(techBlogPost));
                })))
                .build();
    }

    @Bean(TEMP_TECH_BLOG_PROCESS_STEP_NAME)
    @JobScope
    public Step tempTechBlogProcessStep(
            @Value("#{jobParameters[techBlogCode]}") Long code
    ) {
        return new StepBuilder(TEMP_TECH_BLOG_PROCESS_STEP_NAME, jobRepository)
                .<TemporalTechBlogPost, TemporalTechBlogPost>chunk(10, transactionManager)
                .reader(new JpaCursorItemReaderBuilder<TemporalTechBlogPost>()
                        .name("TempTechBlogReader")
                        .queryString("SELECT T FROM TemporalTechBlogPost T where T.techBlogCode = :code")
                        .parameterValues(new HashMap<>() {{
                            this.put("code", TechBlogCode.valueOf(code));
                        }})
                        .entityManagerFactory(entityManagerFactory)
                        .build())
                .writer((chunk -> chunk.getItems().forEach(temporalTechBlogPost -> {
                    final Key key = new Key(temporalTechBlogPost.getUrlSuffix(), temporalTechBlogPost.getTechBlogCode());
                    crawledTechBlogPostRepository.ifPresentOrElse(key,
                            () -> crawledTechBlogPostRepository.remove(key),
                            () -> temporalTechBlogPostRepository.delete(temporalTechBlogPost));
                })))
                .build();
    }


    @Bean(CRAWLER_DATA_SAVE_STEP_NAME)
    Step crawlingDataSaveStep() {
        return new StepBuilder(CRAWLER_DATA_SAVE_STEP_NAME, jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    temporalTechBlogPostRepository.saveAll(crawledTechBlogPostRepository.findAll()
                            .stream()
                            .map(post -> TemporalTechBlogPost.builder()
                                    .title(post.title())
                                    .author(post.author())
                                    .summary(post.summary())
                                    .thumbnailUrl(post.thumbnailUrl())
                                    .url(post.link())
                                    .createdDate(post.postDate())
                                    .techBlogCode(post.code())
                                    .registrationCompleted(false)
                                    .crawledDate(LocalDate.now())
                                    .urlSuffix(post.suffix()).build()).toList());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


}

