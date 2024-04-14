package com.drrr.parser.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.parser.Parser;
import com.drrr.parser.SimpleParser;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BespinGlobalParser {

    @Bean
    Parser bespinGlobalParser(WebDriverPool webDriverPool) {
        return SimpleParser.builder().webDriverPool(webDriverPool)
                .target(By.className("entry-content"))
                .techBlogCode(TechBlogCode.DEV_SISTERS)
                .build();


    }

}
