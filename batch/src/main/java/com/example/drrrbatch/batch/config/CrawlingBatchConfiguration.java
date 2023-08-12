package com.example.drrrbatch.batch.config;


import com.example.drrrbatch.batch.domain.ExternalBlogPosts;
import com.example.drrrbatch.batch.entity.TemporalTechBlogPost;
import com.example.drrrbatch.batch.reader.CrawlerItemReaderFactory;
import com.example.drrrbatch.batch.repository.TemporalTechBlogPostRepository;
import com.example.drrrbatch.batch.vo.TechBlogCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CrawlingBatchConfiguration {
    private static final String BATCH_NAME = "crawling";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CrawlerItemReaderFactory crawlerItemReaderFactory;
    private final TemporalTechBlogPostRepository temporalTechBlogPostRepository;

    @Bean(name = BATCH_NAME + "Job")
    public Job crawlingJob() {
        return new JobBuilder(BATCH_NAME + "Job", jobRepository)
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

}
