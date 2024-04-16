package com.drrr.parser.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.parser.Parser;
import com.drrr.parser.SimpleParser;
import org.openqa.selenium.By;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SquareLabParser {

    @Bean
    Parser squareLabContentsParser(WebDriverPool webDriverPool) {
        return SimpleParser.builder()
                .webDriverPool(webDriverPool)
                .techBlogCode(TechBlogCode.SQUARE_LAB)
                .target(By.className("blog-row"))
                .build();
    }
}
