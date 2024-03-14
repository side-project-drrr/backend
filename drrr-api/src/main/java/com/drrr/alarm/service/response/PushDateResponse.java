package com.drrr.alarm.service.response;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.util.List;
import lombok.Builder;

@Builder
public record PushDateResponse(
        List<TechBlogPostCategoryDto> posts,
        //알림의 특정 푸시 메세지 읽음 여부
        boolean readStatus,
        //알림 아이콘에 느낌표 표시 여부
        boolean openStatus
) {
}
