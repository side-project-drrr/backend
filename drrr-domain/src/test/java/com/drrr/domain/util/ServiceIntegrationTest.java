package com.drrr.domain.util;


import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.email.generator.EmailCodeGenerator;
import com.drrr.domain.jpa.config.JpaConfiguration;
import com.drrr.domain.jpa.config.QueryDSLConfiguration;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@Import({QueryDSLConfiguration.class, DatabaseCleaner.class, JpaConfiguration.class})
public class ServiceIntegrationTest {

    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected  CategoryWeightRepository categoryWeightRepository;
    @Autowired
    protected  TechBlogPostRepository techBlogPostRepository;
    // 이메일 랜덤 코드 생성기
    @MockBean
    protected EmailCodeGenerator emailCodeGenerator;

}
