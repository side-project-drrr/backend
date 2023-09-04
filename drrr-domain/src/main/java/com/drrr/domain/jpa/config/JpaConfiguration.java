package com.drrr.domain.jpa.config;


import com.drrr.domain.Persistence;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackageClasses = Persistence.class)
@EnableJpaRepositories(basePackageClasses = {Persistence.class})
public class JpaConfiguration {
}
