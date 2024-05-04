package com.drrr.web.config;

import com.drrr.domain.rate.service.RateLimiterService;
import com.drrr.web.converter.IndexConverter;
import com.drrr.web.converter.LanguageConverter;
import com.drrr.web.converter.SortDirectionConverter;
import com.drrr.web.converter.TopTechBlogTypeConverter;
import com.drrr.web.interceptor.RateLimitInterceptor;
import com.drrr.web.resolver.JwtTokenResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtTokenResolver jwtTokenResolver;
    private final RateLimiterService rateLimiterService;


    @Override
    public void addFormatters(FormatterRegistry registry) {
        //탑 기술 블로그 타입 컨버터 추가
        registry.addConverter(new TopTechBlogTypeConverter());
        registry.addConverter(new LanguageConverter());
        registry.addConverter(new SortDirectionConverter());
        registry.addConverter(new IndexConverter());

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(rateLimiterService));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(jwtTokenResolver);
    }
}
