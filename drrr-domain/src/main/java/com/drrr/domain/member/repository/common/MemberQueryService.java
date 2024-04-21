package com.drrr.domain.member.repository.common;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberQueryService {
    private final MemberRepository memberRepository;


    public Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> {
            log.error("사용자를 찾을 수 없습니다 => memberId : {}", memberId);
            return DomainExceptionCode.MEMBER_NOT_FOUND.newInstance();
        });
    }

    public List<Member> getAllMembers() {
        final List<Member> members = memberRepository.findAll();

        if (members.size() == 0) {
            log.error("모든 사용자를 찾을 수 없습니다.");
            throw DomainExceptionCode.MEMBER_NOT_FOUND.newInstance();
        }

        return members;
    }

    public Member getMemberByProviderId(final String providerId) {
        return memberRepository.findByProviderId(providerId)
                .orElseThrow(() -> {
                    log.error("provider id에 해당하는 사용자를 찾을 수 없습니다 => providerId : {}", providerId);
                    return DomainExceptionCode.UNREGISTERED_MEMBER.newInstance();
                });
    }
}
