package com.drrr.web.security.config;


import com.drrr.web.jwt.util.JwtProvider;
import com.drrr.web.security.filter.JwtTokenValidationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false)
@Slf4j
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    JwtTokenValidationFilter jwtTokenValidationFilter() {
        return new JwtTokenValidationFilter(jwtProvider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {
        System.out.println("---------security filter chain-------------");
        // http 기본 설정
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> {
                    sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        // 명시적으로 허용할 url 등록
        http.authorizeHttpRequests(
                (auth) -> auth.requestMatchers(
                                mvcMatcherBuilder.pattern("/favicon.ico"),
                                mvcMatcherBuilder.pattern("/login/oauth/kakao/**"),
                                mvcMatcherBuilder.pattern("/h2-console/**"),
                                mvcMatcherBuilder.pattern("/swagger-ui.html"),
                                mvcMatcherBuilder.pattern("/swagger-ui/**"),
                                mvcMatcherBuilder.pattern("/swagger-resources/**"),
                                mvcMatcherBuilder.pattern("/v3/api-docs/**"),
                                mvcMatcherBuilder.pattern("/v2/api-docs"),
                                mvcMatcherBuilder.pattern("/webjars/**"),
                                mvcMatcherBuilder.pattern("/auth/oauth2/profile"),
                                mvcMatcherBuilder.pattern("/auth/signup"),
                                mvcMatcherBuilder.pattern("/auth/signin"),
                                mvcMatcherBuilder.pattern("/auth/code"),
                                mvcMatcherBuilder.pattern("/api/notifications/**"),
                                mvcMatcherBuilder.pattern("/actuator"),
                                mvcMatcherBuilder.pattern("/actuator/**"),
                                mvcMatcherBuilder.pattern("/actuator/prometheus"))
                        .permitAll()
                        .anyRequest()
                        .authenticated()
        );
        /*.oauth2Login((oauth2)-> oauth2.clientRegistrationRepository(clientRegistrationRepository()))*/

        // 필터
        http.addFilterBefore(jwtTokenValidationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    public BasicAuthenticationEntryPoint swaggerAuthenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("Swagger Realm");
        return entryPoint;
    }


}
