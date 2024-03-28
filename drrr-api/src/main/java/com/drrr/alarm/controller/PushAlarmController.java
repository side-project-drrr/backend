package com.drrr.alarm.controller;

import com.drrr.alarm.service.impl.ExternalPushStatusUpdateService;
import com.drrr.alarm.service.impl.ExternalSearchPushPostsCountService;
import com.drrr.alarm.service.impl.ExternalSearchPushPostsService;
import com.drrr.alarm.service.impl.ExternalSubscriptionDeleteService;
import com.drrr.alarm.service.request.PushDateRequest;
import com.drrr.alarm.service.request.PushDatesRequest;
import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.infra.push.dto.PushDateDto;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.SubscriptionRepository;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Secured("USER")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PushAlarmController {
    private final ExternalSearchPushPostsService externalSearchPushPostsService;
    private final ExternalSearchPushPostsCountService externalSearchPushPostsCountService;
    private final SubscriptionRepository subscriptionRepository;
    private final ExternalSubscriptionDeleteService externalSubscriptionDeleteService;
    private final ExternalPushStatusUpdateService externalPushStatusUpdateService;

    @Operation(summary = "count만큼 사용자의 웹 푸시가 존재하는 날을 추출해서 열람여부, 읽음여부, 게시물 개수, 푸시날짜 반환 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시  count만큼 사용자의 웹 푸시가 존재하는 날을 추출해서 열람여부, 읽음여부, 게시물 개수, 푸시날짜 정보 반환")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "사용자의 푸시가 존재하는 날짜별로 count만큼 웹 푸시 상태 정보 반환"))
    @GetMapping("/members/me/web-push/posts/count/{count}")
    public List<PushDateDto> findMemberPushPostsCountByDate(@MemberId final Long memberId, @PathVariable("count") final int count) {
        return externalSearchPushPostsCountService.execute(memberId, count);
    }

    @Operation(summary = "날짜 범위에 해당하는 사용자의 웹 푸시 게시물 정보 반환 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시  pushDate(format : YYYYMMDD) from ~ to에 해당하는 날짜의 푸시 게시물 반환")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "날짜 범위 pushDate(format : YYYYMMDD)에 해당하는 웹 푸시 게시물 정보 반환"))
    @GetMapping("/members/me/web-push/posts/date")
    public List<TechBlogPostCategoryDto> findMemberPushPostsByDate(@MemberId final Long memberId, @Valid @ModelAttribute final PushDateRequest request) {
        return externalSearchPushPostsService.execute(memberId, request);
    }

    @Operation(summary = "지정된 날짜에 해당하는 사용자 웹 푸시를 클릭해서 읽었을 때 호출하는 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 지정된 pushDate(format : YYYYMMDD)에 푸시한 푸시 상태 읽음으로 변경")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "지정된 pushDate(format : YYYYMMDD)에 웹 푸시의 상태를 읽음으로 변경"))
    @PostMapping("/members/me/web-push/posts/read")
    public void updateMemberPushReadStatus(@MemberId final Long memberId, @RequestParam("pushDate") final LocalDate pushDate) {
        externalPushStatusUpdateService.updateReadStatus(memberId, pushDate);
    }

    @Operation(summary = "사용자가 웹푸시 아이콘을 눌렀을 때 호출하는 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 넘겨준 pushDates에 해당하는 push 데이터의 오픈 상태 변경")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "넘겨준 pushDates에 해당하는 push 데이터의 오픈 상태 변경"))
    @PostMapping("/members/me/web-push/posts/open")
    public void updateMemberPushOpenStatus(@MemberId final Long memberId, @RequestBody final PushDatesRequest request) {
        externalPushStatusUpdateService.updateOpenStatus(memberId, request);
    }

    @Operation(summary = "사용자 웹 푸시 구독 정보 저장 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독 정보 저장")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "사용자 웹 푸시 구독 정보 저장 완료"))
    @PostMapping("/members/me/web-push/subscription")
    public void addSubscription(@MemberId final Long memberId, @RequestBody final SubscriptionRequest request) {
        subscriptionRepository.save(
                Subscription.builder()
                        .endpoint(request.endpoint())
                        .auth(request.auth())
                        .p256dh(request.p256dh())
                        .expirationTime(request.expirationTime())
                        .memberId(memberId)
                        .build()
        );
    }

    @Operation(summary = "사용자 웹푸시 구독 정보 삭제 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독 정보 삭제")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "사용자의 웹푸시 구독 정보 삭제 완료"))
    @DeleteMapping("/members/me/web-push/subscription")
    public void cancelSubscription(@MemberId final Long memberId) {
        externalSubscriptionDeleteService.deleteSubscription(memberId);
    }

}
