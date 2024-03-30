package com.drrr.alarm.service.impl;

import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.infra.notifications.kafka.webpush.WebPushProducer;
import com.drrr.infra.push.entity.PushStatus;
import com.drrr.infra.push.repository.PushStatusRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ExternalWebPushTestService {
    private final WebPushProducer webPushProducer;
    private final PushStatusRepository pushStatusRepository;
    public void sendTestWebPush(final Long membeId, final List<Long> postIds, final LocalDate date) {
        pushStatusRepository.save(
                PushStatus.builder()
                .postIds(postIds)
                .readStatus(false)
                .pushDate(date)
                .openStatus(false)
                .pushStatus(true)
                .memberId(membeId)
                .build()
        );

        webPushProducer.sendNotifications();

    }
}
