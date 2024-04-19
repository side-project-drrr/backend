package com.drrr.parser.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.parser.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TechobleParser implements Parser {

    public String execute(final String url) {
        final var document = this.getDocument(url);
        document.select("a").remove();

        document.select("code").remove();
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");

        return document.select(".post-full-content").html();
    }

    @Override
    public TechBlogCode getTechBlogCode() {
        return TechBlogCode.TECHOBLE;
    }
}
