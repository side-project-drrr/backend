package com.drrr.push.config;

import com.drrr.domain.category.dto.PushPostDto;
import com.drrr.infra.notifications.kafka.webpush.dto.NotificationDto;
import com.drrr.infra.push.entity.PushStatus;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PushMessageBatchConfig {
    private final PushStatusRepository pushStatusRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlatformTransactionManager transactionManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public JpaPagingItemReader<Long> batchReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Long>()
                .name("pushTargetReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                            SELECT cw.member.id
                              FROM CategoryWeight cw
                        LEFT OUTER JOIN TechBlogPostCategory tbpc
                                ON cw.category.id = tbpc.category.id
                        LEFT OUTER JOIN TechBlogPost tbp
                                ON tbpc.post.id = tbp.id
                             WHERE tbp.writtenAt = :now
                             GROUP BY cw.member.id
                            """)
                .parameterValues(new HashMap<>() {{
                    this.put("now", LocalDate.now());
                }})
                .pageSize(100)
                .build();
    }

    @Bean("webPushStep")
    @JobScope
    public Step webPushStep() {
        return new StepBuilder("webPushStep", jobRepository)
                //reader의 리턴 타입 List<PushPostDto>,  writer의 리턴 타입 List<PushStatus>
                .<Long, List<PushStatus>>chunk(10, transactionManager)
                .reader(batchReader(entityManagerFactory))
                .processor(new ItemProcessor<Long, List<PushStatus>>() {
                    @Override
                    public List<PushStatus> process(Long memberId) {

                        List<PushPostDto> pushPostDtos = pushStatusRepository.findPushByMemberIds(memberId);

                        return pushPostDtos.stream()
                                .map(pushPostDto -> PushStatus.builder()
                                        .memberId(pushPostDto.memberId())
                                        .pushDate(LocalDate.now())
                                        .pushStatus(false)
                                        .readStatus(false)
                                        .openStatus(false)
                                        .postIds(pushPostDto.postIds())
                                        .build())
                                .toList();
                    }
                })
                .writer(chunk -> {
                    chunk.getItems().stream()
                            .forEach(pushStatuses -> {
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
