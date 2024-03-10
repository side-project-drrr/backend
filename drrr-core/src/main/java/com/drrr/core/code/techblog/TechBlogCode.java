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
    KAKAO(BASE.id + 4, "카카오"),
    DEVOCEAN(BASE.id + 5, "데보션"),
    TECHOBLE(BASE.id + 6, "우테코 기술블로그"),
    NHN_CLOUD(BASE.id + 7, "NHN 클라우드"),
    LINE(BASE.id + 8, "네이버 라인"),
    DEV_SISTERS(BASE.id + 9, "데브시스터즈"),
    BESPIN_GLOBAL(BASE.id + 10, "베스핀글로벌"),
    DAANGN(BASE.id + 11, "당근마켓");

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

