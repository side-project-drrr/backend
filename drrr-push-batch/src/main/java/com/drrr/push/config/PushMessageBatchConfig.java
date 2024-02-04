package com.drrr.push.config;

import com.drrr.domain.category.dto.PushPostDto;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.infra.push.entity.PushStatus;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class PushMessageBatchConfig {
    private final SubscriptionRepository subscriptionRepository;
    private final CategoryWeightRepository categoryWeightRepository;
    private final PlatformTransactionManager transactionManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PushStatusRepository pushStatusRepository;
    private final JobRepository jobRepository;

    @Bean("webPushStep")
    public Step webPushStep() {
        AtomicInteger currentPage = new AtomicInteger(0);
        int pageSize = 100;

        return new StepBuilder("webPushJob", jobRepository).<List<PushPostDto>, List<PushStatus>>chunk(10,
                        transactionManager)
                .reader(() -> {
                    Pageable pageable = PageRequest.of(currentPage.getAndIncrement(), pageSize);
                    Page<PushPostDto> page = categoryWeightRepository.findMemberIdsByCategoryWeights(pageable);
                    
                    if (!page.hasNext()) {
                        return null; // 종료 조건: 다음 페이지가 없으면 null 반환
                    }

                    return page.getContent();
                })
                .processor(new ItemProcessor<List<PushPostDto>, List<PushStatus>>() {
                    @Override
                    public List<PushStatus> process(List<PushPostDto> pushPostDtos) {
                        return pushPostDtos.stream()
                                .map(pushPostDto -> PushStatus.builder()
                                        .memberId(pushPostDto.memberId())
                                        .pushDate(LocalDate.now())
                                        .status(false)
                                        .postIds(pushPostDto.postIds())
                                        .build())
                                .toList();
                    }
                })
                .writer(chunk -> {
                    chunk.getItems().stream()
                            .forEach(pushStatuses -> {
                                pushStatusRepository.saveAll(pushStatuses);

                                List<Subscription> subscriptions = subscriptionRepository.findByMemberIdIn(
                                        pushStatuses.stream()
                                                .map(PushStatus::getMemberId)
                                                .toList()
                                );
                                subscriptions.forEach(subscription ->
                                        kafkaTemplate.send("alarm-web-push", subscription));
                            });
                })
                .build();
    }

    @Bean("memberWebPushJob")
    public Job memberWebPushJob(JobRepository repository) {
        return new JobBuilder("memberWebPushJob", repository)
                .start(webPushStep())
                .build();
    }
}
