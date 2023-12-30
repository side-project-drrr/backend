package com.drrr.web.resolver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 클래스와 메서드 레벨에 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임에도 해당 어노테이션 정보를 유지
public @interface UserId {
}
