package com.drrr.recommand.dto;

import com.drrr.domain.category.entity.Category;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCategoryRequest {
    private Long memberId;
    private List<Category> categories;
}
