package com.example.drrrbatch.batch.config;

import com.example.drrrbatch.batch.domain.ExternalBlogPosts;
import com.example.drrrbatch.batch.entity.TemporalTechBlogPost;
import com.example.drrrbatch.batch.reader.CrawlerItemReaderFactory;
import com.example.drrrbatch.batch.repository.TemporalTechBlogPostRepository;
import com.example.drrrbatch.batch.vo.TechBlogCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.name", havingValue = "crawlingJob")
public class CrawlingBatchConfiguration {
    private static final String BATCH_NAME = "crawling";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TemporalTechBlogPostRepository temporalTechBlogPostRepository;
    private final WebDriver webDriver;

    @Bean(name = BATCH_NAME + "Job")
    public Job crawlingJob() {
        return new JobBuilder(BATCH_NAME + "Job", jobRepository)
                //.incrementer(new RunIdIncrementer()) // 실제 환경에서 지울 것을 권장함
                .start(crawlingStep(null, null))
                .build();
    }

    @Bean(name = BATCH_NAME + "Step")
    @JobScope
    public Step crawlingStep(
            @Value("#{jobParameters[techBlogCode]}") Long code,
            @Value("#{jobParameters[requestDate]}") String requestDate
    ) {
        log.info("get parameter code: {}", code);
        final CrawlerItemReaderFactory crawlerItemReaderFactory = crawlerItemReaderFactory(webDriver);
        return new StepBuilder(BATCH_NAME + "Step", jobRepository)
                .<ExternalBlogPosts, ExternalBlogPosts>chunk(1, transactionManager)
                .reader(crawlerItemReaderFactory.createItemReader(TechBlogCode.valueOf(code)))
                .writer(this::saveChunkToTemporalTechBlogRepository)
                .build();
    }

    private void saveChunkToTemporalTechBlogRepository(Chunk<? extends ExternalBlogPosts> chunk) {
        this.temporalTechBlogPostRepository.saveAll(chunk.getItems()
                .stream()
                .flatMap(externalPosts -> externalPosts.posts()
                        .stream()
                        .map(post -> TemporalTechBlogPost.builder()
                                .title(post.title())
                                .author(post.author())
                                .summary(post.summary())
                                .thumbnailUrl(post.thumbnailUrl())
                                .url(post.link())
                                .createdDate(post.postDate())
                                .techBlogCode(post.code())
                                .urlSuffix(post.suffix()).build())
                ).toList());
    }

    @Bean
    public CrawlerItemReaderFactory crawlerItemReaderFactory(WebDriver webDriver) {
        return new CrawlerItemReaderFactory(webDriver);
    }

}
