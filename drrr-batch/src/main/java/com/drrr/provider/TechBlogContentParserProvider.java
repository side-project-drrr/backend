package com.drrr.provider;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.parser.TechobleParser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TechBlogContentParserProvider {

    private static final int MAX_PARAGRAPH_LENGTH = 1024;
    private final TechobleParser techobleParser;


    public List<String> execute(TechBlogCode code, String url) {
        return this.divideTextIntoParagraphs(techobleParser.execute(url).replaceAll("\\\\n", "\n"));
    }

    private List<String> divideTextIntoParagraphs(String text) {
        List<String> paragraphs = new ArrayList<>();

        String[] words = text.split("\n\n");
        StringBuilder currentParagraph = new StringBuilder();

        for (String word : words) {
            word = Jsoup.parse(word).text();
            if (currentParagraph.length() + word.length() + 1 <= MAX_PARAGRAPH_LENGTH) {
                if (!currentParagraph.isEmpty()) {
                    currentParagraph.append(" ");
                }
                currentParagraph.append(word);

                continue;
            }

            paragraphs.add(currentParagraph.toString());
            currentParagraph = new StringBuilder(word);
        }

        if (!currentParagraph.isEmpty()) {
            paragraphs.add(currentParagraph.toString());
        }

        return paragraphs;
    }
}
