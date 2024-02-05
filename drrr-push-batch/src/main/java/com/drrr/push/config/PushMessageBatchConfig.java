package com.drrr.push.config;

import com.drrr.domain.category.dto.PushPostDto;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.infra.notifications.kafka.webpush.dto.NotificationDto;
import com.drrr.infra.push.entity.PushStatus;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
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
@Configuration
public class PushMessageBatchConfig {
    private final SubscriptionRepository subscriptionRepository;
    private final CategoryWeightRepository categoryWeightRepository;
    private final PlatformTransactionManager transactionManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PushStatusRepository pushStatusRepository;
    private final JobRepository jobRepository;

    @Bean("memberWebPushStep")
    @JobScope
    public Step webPushStep() {
        AtomicInteger currentPage = new AtomicInteger(0);
        AtomicBoolean lastPage = new AtomicBoolean(false);
        int pageSize = 100;

        return new StepBuilder("memberWebPushStep", jobRepository).<List<PushPostDto>, List<PushStatus>>chunk(10,
                        transactionManager)
                .reader(() -> {
                    if (lastPage.get()) {
                        return null;
                    }

                    Pageable pageable = PageRequest.of(currentPage.getAndIncrement(), pageSize);
                    Page<PushPostDto> page = categoryWeightRepository.findMemberIdsByCategoryWeights(pageable);

                    if (!page.hasNext()) {
                        lastPage.set(true); // 종료 조건: 다음 페이지가 없으면 null 반환
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
                                subscriptions.stream().forEach(subscription -> {
                                    NotificationDto notification = NotificationDto.builder()
                                            .id(subscription.getId())
                                            .endpoint(subscription.getEndpoint())
                                            .p256dh(subscription.getP256dh())
                                            .payload("DRRR에서 새로 업데이트된 게시물을 만나보세요!")
                                            .auth(subscription.getAuth())
                                            .memberId(subscription.getMemberId())
                                            .build();
                                    this.kafkaTemplate.send("alarm-web-push", notification);
                                });
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
