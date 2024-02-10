package com.drrr.member.controller;

import com.drrr.domain.member.dto.MemberDto;
import com.drrr.member.service.ExternalMemberService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MemberController {
    private final ExternalMemberService externalMemberService;

    @Operation(summary = "사용자 정보 반환 API",
            description = "호출 성공 시 사용자 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 반환", content = @Content(schema = @Schema(implementation = MemberDto.class)))
    })
    @GetMapping("/member/profile")
    public MemberDto findMemberInfo(@MemberId final Long memberId) {
        return externalMemberService.execute(memberId);
    }
}
