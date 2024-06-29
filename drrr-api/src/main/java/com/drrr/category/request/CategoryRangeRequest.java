package com.drrr.category.request;

import com.drrr.core.category.constant.CategoryTypeConstants;
import com.drrr.core.category.constant.IndexConstants;
import com.drrr.domain.exception.DomainExceptionCode;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategoryRangeRequest(
        @NotNull IndexConstants startIdx,
        @NotNull IndexConstants endIdx,
        @NotNull CategoryTypeConstants language,
        @NotNull int size
) {
    public static void requestValidationCheck(final CategoryRangeRequest request) {
        if (request.language.equals(CategoryTypeConstants.ENGLISH) &&
                (!isEnglish(request.startIdx.getCharacter()) || !isEnglish(request.endIdx.getCharacter()))) {
            throw DomainExceptionCode.INVALID_LANGUAGE_CHARACTER_INDEX.newInstance();
        }
        if (request.language.equals(CategoryTypeConstants.KOREAN) &&
                (!isKorean(request.startIdx.getCharacter()) || !isKorean(request.endIdx.getCharacter()))) {
            throw DomainExceptionCode.INVALID_LANGUAGE_CHARACTER_INDEX.newInstance();
        }
        //순서 검사
        if (request.startIdx.ordinal() > request.endIdx.ordinal()) {
            throw DomainExceptionCode.RANGE_PARAMTERS_OUT_OF_ORDER.newInstance();
        }
    }

    private static boolean isEnglish(final Character index) {
        return index >= 'A' && index <= 'Z';
    }

    private static boolean isKorean(final Character index) {
        return index >= '\uAC00' && index <= '\uD7A3';
    }
}
