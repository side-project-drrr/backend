package com.drrr.parser.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.parser.Parser;
import com.drrr.parser.SimpleParser;
import org.openqa.selenium.By;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BespinGlobalParser {

    @Bean
    Parser bespinGlobalContentParser(WebDriverPool webDriverPool) {
        return SimpleParser.builder().webDriverPool(webDriverPool)
                .target(By.className("entry-content"))
                .techBlogCode(TechBlogCode.BESPIN_GLOBAL)
                .build();
    }

}
