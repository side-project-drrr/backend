package com.drrr.parser;


import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TechobleParser {


    public String execute(String url) {
        final var document = this.getDocument(url);
        document.select("a").remove();

        document.select("code").remove();
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");

        return document.select(".post-full-content").html();
    }

    Document getDocument(String url) {
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);

        System.out.println(url);

        try {
            return Jsoup.connect(String.valueOf(new URL(url).toExternalForm())).get();
        } catch (IOException e) {
            log.error("{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
