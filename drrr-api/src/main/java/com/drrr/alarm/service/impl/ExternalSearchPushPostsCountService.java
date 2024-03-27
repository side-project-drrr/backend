package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.PushDateRequest;
import com.drrr.infra.push.dto.PushDateDto;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.service.PushService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExternalSearchPushPostsCountService {
    private final PushService pushService;

    public List<PushDateDto> execute(final Long memberId, final int count) {
        return pushService.findPushesByCount(memberId, count);
    }
}
