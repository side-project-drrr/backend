package com.drrr.member.service;

import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.member.dto.PostReadCheckDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExternalMemberPostReadCheckService {
    private final MemberPostLogRepository memberPostLogRepository;

    public PostReadCheckDto execute(Long memberId, Long postId) {
        Boolean isRead = memberPostLogRepository.checkMemberPostRead(memberId, postId).orElse(false);

        return PostReadCheckDto.builder()
                .isRead(isRead)
                .postId(postId)
                .build();
    }
}
