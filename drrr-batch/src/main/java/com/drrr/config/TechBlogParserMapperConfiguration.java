package com.drrr.config;

import static java.util.stream.Collectors.toMap;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.parser.Parser;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TechBlogParserMapperConfiguration {

    @Bean
    Map<TechBlogCode, Parser> techBlogCodeParserMap(List<Parser> parsers) {
        return parsers.stream()
                .collect(toMap(
                        Parser::getTechBlogCode,
                        Function.identity(),
                        (prev, next) -> {
                            throw new IllegalStateException("중복된 기술블로그 본문 파서가 등록됐습니다.");
                        })
                );

    }
}
