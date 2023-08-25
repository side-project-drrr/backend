package com.example.batch.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TechBlogCodeTest {


    @Test
    void 기술블로그_아이디를_통해서_기술블로그를_정상적으로_가져오는가() {
        final var base = TechBlogCode.BASE;

        assertThat(TechBlogCode.valueOf(base.getId())).isEqualTo(base);
        assertThat(TechBlogCode.valueOf(101L)).isEqualTo(TechBlogCode.MARKET_KURLY);
    }

}