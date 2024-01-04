package com.drrr.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.TYPE, ElementType.METHOD}) // 클래스와 메서드 레벨에 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임에도 해당 어노테이션 정보를 유지
@PreAuthorize("hasAuthority('USER')")//JWT 토큰이 없고 security filter chain 명시가 되어 있는 경우
public @interface UserAuthority {
}
