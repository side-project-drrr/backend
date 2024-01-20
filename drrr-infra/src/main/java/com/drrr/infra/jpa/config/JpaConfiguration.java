package com.drrr.infra.jpa.config;


import com.drrr.infra.Persistence;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("infraJpaConfiguration")
//@EnableJpaAuditing
@EntityScan(basePackageClasses = Persistence.class)
@EnableJpaRepositories(basePackageClasses = {Persistence.class})
public class JpaConfiguration {
}


