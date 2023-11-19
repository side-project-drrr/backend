package com.drrr.domain.jpa.config;


import com.drrr.domain.Persistence;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackageClasses = Persistence.class)
@EnableJpaRepositories(basePackageClasses = {Persistence.class})
public class JpaConfiguration {
    @Bean
    public AuditorAware<LocalDateTime> auditorProvider() {
        return () -> Optional.of(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }
}


