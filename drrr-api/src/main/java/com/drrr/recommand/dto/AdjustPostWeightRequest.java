package com.drrr.recommand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AdjustPostWeightRequest(
        @Schema(description = "카테고리 id", nullable = false, example = "[1, 2, 3]")
        List<@NotNull(message = "사용자의 선호 카테고리id를 지정해주세요") Long> categoryIds
) {
}
