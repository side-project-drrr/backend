package com.drrr.provider;


import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TextLineSplitCategoryProvider {

    private static final int MAXIMUM_CATEGORY_LENGTH = 80; // 카테고리명으로 선택할 수 있는 최대 길이
    private static final int MAXIMUM_CATEGORY_SIZE = 10; // 선택할 수 있는 최대 카테고리 개수

    /**
     * @formatter:off
     *
     * 문자열에서 카테고리를 추출 하는 두 가지 방식이 있습니다.
     * 첫 번쨰 분리 방식은
     * ex) 1.~, 2.~
     * 위와 같이 한줄에 위치한 경우입니다.
     *
     * 두 번째 분리 방식은
     * 1.~
     * 2.~
     * 3.~
     * 위와 같이 각 줄에 걸쳐서 위치한 경우입니다.
     *
     * 추가적으로 고려할 점은
     * 한 줄에 너무 긴 문자열이 존재한 경우 -> 이 경우는 해당 문자열을 정상적이지 않은 문자열로 판단합니다.
     * 두 번째는 선택하는 최소 카테고리 개수입니다 -> 이 경우는 우선순위를 둬서 만들어진 카테고리 목록이기 때문에 지정한 개수 이상인 경우는 우선순위 밖의 카테고리로 판단해서 제외합니다.
     *
     * @formatter:on
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
