package com.drrr.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DomainExceptionCode {
    OAUTH(1500, "정의되지 않은 에러입니다."),
    INVALID_ACCESS_TOKEN(OAUTH.code + 1, "OAuth2 Access Token이 유효하지 않습니다."),
    INVALID_AUTHORIZE_CODE(OAUTH.code + 2, "OAuth2 Authorize Token이 유효하지 않습니다."),
    INVALID_OAUTH2_SUBJECT(OAUTH.code + 3, "OAuth2 주체를 찾을 수 없습니다."),
    PROVIDER_ID_NULL(OAUTH.code + 4, "Provider Id를 찾을 수 없습니다."),
    TECH_BLOG(2000, "정의되지 않은 에러입니다."),
    TECH_BLOG_NOT_FOUND(TECH_BLOG.code + 1, "기술블로그를 찾을 수 없습니다."),
    CATEGORY(2500, "정의되지 않은 에러입니다."),
    CATEGORY_NOT_FOUND(CATEGORY.code + 1, "카테고리를 찾을 수 없습니다."),
    CATEGORY_WEIGHT_NOT_FOUND(CATEGORY.code + 2, "카테고리 가중치 정보를 찾을 수 없습니다."),
    MEMBER_CATEGORY_UNCHANGD(CATEGORY.code + 3, "수정된 카테고리가 없습니다."),
    LIKE(3000, "정의되지 않은 에러입니다."),
    DUPLICATE_LIKE(LIKE.code + 1, "사용자가 하나의 게시물에 중복으로 좋아요를 눌렀습니다."),
    EMAIL(3500, "정의되지 않은 에러입니다."),
    EMAIL_VERIFICATION_INFORMATION_NOT_FOUND(EMAIL.code + 1, "이메일 인증 정보를 찾을 수 없습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(EMAIL.code + 2, "이메일 인증코드가 유효하지 않습니다."),
    LOGGING(4000, "정의되지 않은 에러입니다."),
    INVALID_RECOMMEND_POSTS_LOGGING(LOGGING.code + 1, "기술 블로그 추천 후 로깅이 제대로 동작하지 않습니다."),
    MEMBER(4500, "정의되지 않은 에러입니다."),
    DUPLICATE_EMAIL(MEMBER.code + 4, "이메일이 이미 등록되어 있습니다."),
    DUPLICATE_NICKNAME(MEMBER.code + 5, "닉네임이 이미 등록되어 있습니다."),
    UNKNOWN_ERROR(MEMBER.code + 6, "알 수 없는 오류가 발생했습니다."),
    UNREGISTERED_MEMBER(MEMBER.code + 7, "등록되지 않은 사용자입니다."),
    INVALID_MEMBER_WEIGHT(MEMBER.code + 8, "사용자 가중치를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(MEMBER.code + 9, "사용자를 찾을 수 없습니다."),
    ;
    private final int code;
    private final String message;

    public DomainException newInstance() {
        return new DomainException(code, message);
    }

    public DomainException newInstance(Throwable ex) {
        return new DomainException(code, message, ex);
    }

    public DomainException newInstance(Object... args) {
        return new DomainException(code, String.format(message, args), args);
    }

    public DomainException newInstance(Throwable ex, Object... args) {
        return new DomainException(code, String.format(message, args), ex, args);
    }
}
