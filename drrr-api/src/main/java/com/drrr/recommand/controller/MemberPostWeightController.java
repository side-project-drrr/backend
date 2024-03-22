package com.drrr.recommand.controller;

import com.drrr.recommand.service.impl.ExternalMemberPostReadService;
import com.drrr.web.annotation.MemberId;
import com.drrr.web.annotation.swagger.SwaggerDocHeaderParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MemberPostWeightController {
    private final ExternalMemberPostReadService memberPostReadService;

    @Operation(summary = "사용자가 특정 기술 블로그를 읽으려고 클릭했을 때 요청하는 API - [JWT TOKEN REQUIRED]"
            , description = "호출 성공 시 조회한 기술 블로그 기준으로 가중치 계산, 로깅, 조회수 증가",
            parameters = {
                    @Parameter(name = "postId", description = "게시물 ID", in = ParameterIn.PATH, schema = @Schema(type = "string"))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회한 기술 블로그 기준으로 가중치 계산, 로깅, 조회수 증가"
                    , content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @Secured("USER")
    @PostMapping("/members/me/read-post/{postId}")
    public void MemberPostReadController(
            @SwaggerDocHeaderParam
            @MemberId final Long memberId,
            @NotNull @PathVariable(name = "postId") final Long postId
    ) {
        memberPostReadService.execute(memberId, postId);
    }

}
