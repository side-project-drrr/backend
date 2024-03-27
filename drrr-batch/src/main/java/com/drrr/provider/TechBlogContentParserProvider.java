package com.drrr.provider;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.parser.Parser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TechBlogContentParserProvider {

    private static final int MAX_PARAGRAPH_LENGTH = 1024;
    private final Map<TechBlogCode, Parser> techBlogCodeParsers;


    public List<String> execute(TechBlogCode code, String url) {
        var postContent = techBlogCodeParsers.get(code)
                .execute(url)
                .replaceAll("\\\\n", "\n");

        return this.divideTextIntoParagraphs(postContent)
                .stream()
                .filter(text -> !text.isBlank())
                .map(text -> text.replaceAll("\0", ""))
                .toList();
    }

    // 본문을 문단별로 분리합니다.
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
