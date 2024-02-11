package com.drrr.member.controller;

import com.drrr.domain.member.dto.MemberDto;
import com.drrr.member.dto.PostReadCheckDto;
import com.drrr.member.service.ExternalMemberPostReadCheckService;
import com.drrr.member.service.ExternalMemberService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MemberController {
    private final ExternalMemberService externalMemberService;
    private final ExternalMemberPostReadCheckService externalMemberPostReadCheckService;

    @Operation(summary = "사용자 정보 반환 API",
            description = "호출 성공 시 사용자 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 반환", content = @Content(schema = @Schema(implementation = MemberDto.class)))
    })
    @GetMapping("/member/me")
    public MemberDto findMemberInfo(@MemberId final Long memberId) {
        return externalMemberService.execute(memberId);
    }

    @Operation(summary = "사용자 게시글 읽음 여부 반환 API",
            description = "호출 성공 시 사용자 게시글 읽음 여부 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 게시글 읽음 여부 반환", content = @Content(schema = @Schema(implementation = PostReadCheckDto.class)))
    })
    @GetMapping("/member/post/{postId}/read")
    public PostReadCheckDto checkPostRead(@PathVariable("postId") final Long postId, @MemberId final Long memberId) {
        return externalMemberPostReadCheckService.execute(memberId, postId);
    }
}
