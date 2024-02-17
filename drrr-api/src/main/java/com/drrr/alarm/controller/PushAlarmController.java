package com.drrr.alarm.controller;

import com.drrr.alarm.dto.PushRequest;
import com.drrr.alarm.service.impl.ExternalDeleteSubscriptionService;
import com.drrr.alarm.service.impl.ExternalMemberSubscriptionService;
import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

    @Operation(summary = "지정된 날짜에 해당하는 사용자 웹 푸시 블로그 정보 반환  API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 지정된 pushDate(format : YYYYMMDD)에 푸시 해준 사용자의 블로그 정보 반환(작성일 내림차순)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지정된 pushDate(format : YYYYMMDD)에 웹 푸시를 해준 사용자의 블로그 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostCategoryDto.class))))
    })
    @GetMapping("/members/me/web-push/posts")
    public List<TechBlogPostCategoryDto> findMemberPushPosts(@Valid @ModelAttribute final PushRequest request) {
        return externalMemberSubscriptionService.execute(request.memberId(), request.pushDate());
    }

    @Operation(summary = "사용자 웹 푸시 구독 정보 저장 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독 정보 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 웹 푸시 구독 정보 저장 완료", content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @PostMapping("/members/me/web-push/subscription")
    public void addSubscription(@MemberId final Long memberId, @RequestBody final SubscriptionRequest request) {
        externalMemberSubscriptionService.execute(request, memberId);
    }

    @Operation(summary = "사용자 웹푸시 구독 정보 삭제 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독 정보 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 웹푸시 구독 정보 삭제 완료", content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @DeleteMapping("/members/me/web-push/subscription")
    public void cancelSubscription(@MemberId final Long memberId) {
        externalDeleteSubscriptionService.execute(memberId);
    }

}
