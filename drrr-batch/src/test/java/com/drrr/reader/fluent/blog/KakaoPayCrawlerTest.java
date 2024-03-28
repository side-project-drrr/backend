package com.drrr.reader.fluent.blog;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class KakaoPayCrawlerTest {


    @Test
    @Disabled
    void 크롤링_테스트() throws Exception {
        var kakaoPayCrawler = new KakaoPayCrawler();

        try (var pool = kakaoPayCrawler.webDriverPool()) {

            var reader = kakaoPayCrawler.kakaoPayReader(pool);

            while (reader.read() != null) {

            }
        }

        //count 3 - 26sec
        //count 9 - 36sec
        //count 5 - 21sec

    }

}