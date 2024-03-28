package com.drrr.member.controller;

import com.drrr.domain.member.dto.MemberDto;
import com.drrr.member.dto.PostReadCheckDto;
import com.drrr.member.payload.request.ProfileUpdateRequest;
import com.drrr.member.service.ExternalMemberPostReadCheckService;
import com.drrr.member.service.ExternalMemberService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MemberController {
    private final ExternalMemberService externalMemberService;
    private final ExternalMemberPostReadCheckService externalMemberPostReadCheckService;

    @Operation(summary = "사용자 정보 반환 API", description = "호출 성공 시 사용자 정보 반환")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "사용자 정보 반환"))
    @GetMapping("/members/me")
    public MemberDto findMemberInfo(@MemberId final Long memberId) {
        return externalMemberService.execute(memberId);
    }

    @Operation(summary = "사용자가 과거에 request에 담긴 postId에 해당하는 게시글을 읽었는지 여부 반환 API", description = "호출 성공 시 사용자가 postId에 해당하는 게시글을 읽었는지 여부 반환")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "사용자가 postId에 해당하는 게시글을 읽었는지 여부 반환"))
    @GetMapping("/members/me/past/read-post/{postId}")
    public PostReadCheckDto checkPostRead(@PathVariable("postId") final Long postId, @MemberId final Long memberId) {
        return externalMemberPostReadCheckService.execute(memberId, postId);
    }

    @Operation(summary = "사용자의 이메일 또는 닉네임 변경 API", description = "호출 성공 시 사용자의 이메일 또는 닉네임 변경")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "사용자의 이메일 또는 닉네임 변경"))
    @PostMapping("/members/me/update-profile")
    public void updateProfile(@Valid @RequestBody final ProfileUpdateRequest request, @MemberId final Long memberId) {
        externalMemberService.execute(memberId, request);
    }
}
