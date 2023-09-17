package com.drrr.domain.category.service;

import static org.junit.jupiter.api.Assertions.*;

import com.drrr.domain.jpa.config.JpaConfiguration;
import com.drrr.domain.jpa.config.QuerydslConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@Import(QuerydslConfiguration.class)
class WeightValidationServiceTest {

    @Test
    void 테스트(){

    }

}