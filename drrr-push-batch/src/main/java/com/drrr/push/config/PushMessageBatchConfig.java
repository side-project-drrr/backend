package com.drrr.push.config;

import com.drrr.domain.category.dto.PushPostDto;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.infra.push.entity.PushPost;
import com.drrr.infra.push.entity.PushStatus;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.PushPostRepository;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final PushPostRepository pushPostRepository;
    private final JobRepository jobRepository;

    @Bean("webPushStep")
    public Step webPushStep() {
        Pageable pageable = PageRequest.of(0, 100);

        return new StepBuilder("webPushJob", jobRepository).<List<Subscription>, List<Subscription>>chunk(10,
                        transactionManager)
                .reader(() -> {
                    List<PushPostDto> pushPostDtos = categoryWeightRepository.findMemberIdsByCategoryWeights(pageable)
                            .getContent();
                    pageable.next();

                    //PushStatus 저장
                    List<PushStatus> pushStatuses = pushPostDtos.stream()
                            .map((pushPostDto) -> {
                                return PushStatus.builder()
                                        .memberId(pushPostDto.memberId())
                                        .pushDate(LocalDate.now())
                                        .status(false)
                                        .build();
                            }).toList();

                    pushStatusRepository.saveAll(pushStatuses);

                    //PushPost 저장
                    pushPostDtos.stream()
                            .forEach((pushPostDto) -> {
                                List<PushPost> pushPosts = pushPostDto.postIds().stream()
                                        .map((postId) -> {
                                            return PushPost.builder()
                                                    .postId(postId)
                                                    .pushStatus(
                                                            PushStatus.builder()
                                                                    .memberId(pushPostDto.memberId())
                                                                    .pushDate(LocalDate.now())
                                                                    .status(true)
                                                                    .build()
                                                    )
                                                    .build();
                                        }).toList();
                                pushPostRepository.saveAll(pushPosts);
                            });

                    return subscriptionRepository.findByMemberIdIn(
                            pushPostDtos.stream()
                                    .map(PushPostDto::memberId)
                                    .toList()
                    );
                })
                .writer((chunk) -> chunk.getItems().forEach(subscription -> {
                    kafkaTemplate.send("alarm-web-push", subscription);
                }))
                .build();
    }

    @Bean("memberWebPushJob")
    public Job memberWebPushJob(JobRepository repository) {
        return new JobBuilder("memberWebPushJob", repository)
                .start(webPushStep())
                .build();
    }
}
