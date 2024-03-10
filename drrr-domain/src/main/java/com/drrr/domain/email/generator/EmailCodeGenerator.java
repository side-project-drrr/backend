package com.drrr.domain.email.generator;

import java.util.UUID;
import org.springframework.stereotype.Component;


@Component
public class EmailCodeGenerator implements RandomCodeGenerator {

    private static final int START_INDEX = 0;
    private static final int END_INDEX = 6;

    @Override
    public String generate() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(START_INDEX, END_INDEX);
    }
}
