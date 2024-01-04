package com.drrr.domain.member.service;

import com.drrr.domain.member.dto.MemberExistDto;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExistenceMemberService {
    private final MemberRepository memberRepository;

    public boolean checkMemberExists(final String providerId){
        final Optional<Member> optionalMember = memberRepository.findByProviderId(providerId);
        return optionalMember.isPresent();
    }
}
