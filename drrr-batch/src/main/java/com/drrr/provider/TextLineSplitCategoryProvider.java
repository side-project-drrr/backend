package com.drrr.provider;


import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TextLineSplitCategoryProvider {

    private static final int MAXIMUM_CATEGORY_LENGTH = 80; // 카테고리명으로 선택할 수 있는 최대 길이
    private static final int MAXIMUM_CATEGORY_SIZE = 10; // 선택할 수 있는 최대 카테고리 개수

    /**
     * 분리 방식 1.~, 2.~
     * <p>
     * 1.~ <br>2.~ <br>3.~
     */
    private static final int TEXT_LINE_DELIMITER_SIZE = 2;

    public List<String> execute(String content) {
        String[] arrWithSplitN = content.split("\n");

        if (arrWithSplitN.length >= TEXT_LINE_DELIMITER_SIZE) {
            return textLineToCategories(arrWithSplitN);
        }

        return textLineToCategories(content.replace("키워드:", "").split(","));
    }

    private List<String> textLineToCategories(String[] textLine) {
        return Arrays.stream(textLine)
                .map(text -> text.replace("-", ""))
                .map(text -> text.replaceAll("(\\d+\\.)\\s*", ""))
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .filter(text -> text.length() < MAXIMUM_CATEGORY_LENGTH)
                .limit(MAXIMUM_CATEGORY_SIZE)
                .toList();
    }
}
