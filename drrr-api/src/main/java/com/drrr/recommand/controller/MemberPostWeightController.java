package com.drrr.recommand.controller;

import com.drrr.recommand.dto.AdjustPostWeightRequest;
import com.drrr.recommand.service.impl.ExternalMemberPostReadService;
import com.drrr.web.security.annotation.UserAuthority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@UserAuthority
@RequestMapping("/api/v1")
public class MemberPostWeightController {
    private final ExternalMemberPostReadService memberPostReadService;

    @Operation(summary = "사용자가 특정 기술 블로그를 읽으려고 클릭했을 때 요청하는 API", description = "호출 성공 시 조회한 기술 블로그 기준으로 가중치 계산, 로깅, 조회수 증가",
            parameters = {
                    @Parameter(name = "memberId", description = "게시물을 읽은 사용자 ID", in = ParameterIn.PATH, schema = @Schema(type = "string")),
                    @Parameter(name = "postId", description = "게시물 ID", in = ParameterIn.PATH, schema = @Schema(type = "string"))
            })
    @PostMapping("/posts/read/{memberId}/{postId}")
    public ResponseEntity<String> MemberPostReadController(
            @Validated @RequestBody final AdjustPostWeightRequest request,
            @NonNull @PathVariable(name = "memberId") final Long memberId,
            @NonNull @PathVariable(name = "postId") final Long postId) {
        memberPostReadService.execute(request, memberId, postId);
        return ResponseEntity.ok().build();
    }

}
