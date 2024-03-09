package com.drrr.domain.member;

import com.drrr.domain.member.entity.Member;
import com.drrr.domain.util.ServiceIntegrationTest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemberFixture extends ServiceIntegrationTest {
    private final static int MEMBER_COUNT = 20;

    public static List<Member> createMembers() {
        return IntStream.range(0, MEMBER_COUNT).mapToObj(i -> Member.builder()
                .providerId("providerId" + i)
                .nickname("nickname" + i)
                .profileImageUrl("profileImageUrl" + i)
                .email("email" + i)
                .provider("provider" + i)
                .isActive(true)
                .build()).collect(Collectors.toList());
    }
}
