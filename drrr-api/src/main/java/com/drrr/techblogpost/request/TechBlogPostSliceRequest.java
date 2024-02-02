package com.drrr.techblogpost.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechBlogPostSliceRequest {
    @Default
    private final int page = 0;
    @Default
    private final int size = 10;
    @Default
    private final String sort = "writtenAt";
    @Default
    private final String direction = "DESC";

}
