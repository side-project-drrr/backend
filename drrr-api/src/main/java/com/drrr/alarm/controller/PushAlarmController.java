package com.drrr.alarm.controller;

import com.drrr.alarm.dto.PushRequest;
import com.drrr.alarm.service.impl.ExternalDeleteSubscriptionService;
import com.drrr.alarm.service.impl.ExternalMemberSubscriptionService;
import com.drrr.alarm.service.impl.ExternalUpdatePushStatusService;
import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.web.annotation.JwtToken;
import com.drrr.web.jwt.util.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Secured("USER")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PushAlarmController {
    private final ExternalDeleteSubscriptionService externalDeleteSubscriptionService;
    private final ExternalMemberSubscriptionService externalMemberSubscriptionService;
    private final ExternalUpdatePushStatusService externalUpdatePushStatusService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "사용자가 푸시 알림 클릭 시 보여줄 블로그 정보 반환 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 푸시 알림 클릭 시 보여줄 블로그 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 푸시 알림 클릭 시 보여줄 블로그 정보 반환", content = @Content(schema = @Schema(implementation = TechBlogPostCategoryDto.class)))
    })
    @GetMapping("/push/posts/member")
    public List<TechBlogPostCategoryDto> findMemberPushPosts(@Valid @ModelAttribute final PushRequest request) {
        return externalMemberSubscriptionService.execute(request.memberId(), request.pushDate());
    }

    @Operation(summary = "사용자가 푸시 알림 클릭 시 호출 - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 푸시 알림 상태를 변경 (읽음 처리)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 푸시 알림 상태를 변경", content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @PatchMapping("/push/status/read")
    public void changeMemberPushStatus(@RequestBody final PushRequest request) {
        externalUpdatePushStatusService.execute(request.memberId(), request.pushDate());
    }


    @Operation(summary = "구독 요청 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독을 요청함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 구독 신청 완료", content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @PostMapping("/member/subscription")
    public void addSubscription(@JwtToken final Long memberId, @RequestBody final SubscriptionRequest request) {
        externalMemberSubscriptionService.execute(request, memberId);
    }

    @Operation(summary = "구독 취소 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독을 취소함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 구독 취소 완료", content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @DeleteMapping("/member/subscription")
    public void cancelSubscription(@JwtToken final Long memberId) {
        externalDeleteSubscriptionService.execute(memberId);
    }

}
