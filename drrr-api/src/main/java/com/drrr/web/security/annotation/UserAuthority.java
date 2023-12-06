package com.drrr.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.TYPE) // 클래스 단위로 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임에도 해당 어노테이션 정보를 유지
@PreAuthorize("hasAuthority('USER')")
public @interface UserAuthority {
}
