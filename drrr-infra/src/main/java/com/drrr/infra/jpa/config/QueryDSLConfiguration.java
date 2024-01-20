package com.drrr.infra.jpa.config;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("infraQueryDSLConfiguration")
public class QueryDSLConfiguration {
    @PersistenceContext
    private EntityManager entityManager;
}
