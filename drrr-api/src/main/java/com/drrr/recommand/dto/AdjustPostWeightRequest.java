package com.drrr.recommand.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class AdjustPostWeightRequest {
    private Long memberId;
    private Long postId;
    private List<Long> categoryIds;
}
