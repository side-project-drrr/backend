package com.drrr.recommand.controller;

import com.drrr.recommand.service.impl.ExternalMemberPreferredCategoryModificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/category/modification")
public class MemberPreferredCategoryModificationController {
    private final ExternalMemberPreferredCategoryModificationService modificationService;

    @Operation(summary = "사용자가 선호카테고리를 바꿀 때 호출하는 API", description = "호출 성공 시 사용자의 선호나는 카테고리 변경",
            parameters = {
                    @Parameter(name = "memberId", description = "사용자 ID", in = ParameterIn.PATH, schema = @Schema(type = "string")),
                    @Parameter(name = "categoryIds", description = "카테고리 ID 리스트, body 안에 포함 ex: [1, 2, 3] 이렇게 객체로 둘러싸지 말고 리스트 형태로 body에 넣어줄 것", schema = @Schema(type = "array", implementation = Long.class))
            })
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/{memberId}")
    public ResponseEntity<String> modifyCategory(@NonNull @PathVariable(name = "memberId") final Long memberId,
                                                 @RequestBody @NonNull final List<Long> categoryIds) {
        modificationService.execute(memberId, categoryIds);
        return ResponseEntity.ok().build();
    }
}
