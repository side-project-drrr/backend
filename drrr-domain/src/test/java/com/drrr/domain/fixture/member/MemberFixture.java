package com.drrr.domain.fixture.member;

import com.drrr.domain.member.entity.Member;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemberFixture {

    public static List<Member> createMembers(final int count) {
        return IntStream.range(0, count).mapToObj(i -> Member.builder()
                .providerId("providerId" + i)
                .nickname("nickname" + i)
                .profileImageUrl("profileImageUrl")
                .email("email" + i)
                .provider("provider")
                .isActive(true)
                .build()).collect(Collectors.toList());
    }

    public static Member createMember() {
        return Member.builder()
                .providerId("providerId")
                .nickname("nickname")
                .profileImageUrl("profileImageUrl")
                .email("email")
                .provider("provider")
                .isActive(true)
                .build();
    }
}
