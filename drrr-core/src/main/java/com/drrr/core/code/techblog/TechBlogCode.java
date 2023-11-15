package com.drrr.core.code.techblog;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechBlogCode {
    BASE(100L, ""), // 기본 상태
    MARKET_KURLY(BASE.id + 1, "마켓 컬리"),
    NAVER(BASE.id + 2, "네이버"),
    WOOWAHAN(BASE.id + 3, "우아한 형제들"),
    KAKAO(BASE.id + 4, "카카오");

    // enum 성능 최적화
    private static final Map<Long, TechBlogCode> cache = Arrays.stream(values())
            .collect(Collectors.toMap(TechBlogCode::getId, Function.identity()));

    private final Long id;
    private final String name;

    public static TechBlogCode valueOf(Long id) {
        return Optional.ofNullable(cache.get(id))
                .orElse(BASE);
    }

}

