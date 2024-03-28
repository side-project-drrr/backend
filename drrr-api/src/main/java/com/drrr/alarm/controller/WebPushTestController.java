package com.drrr.alarm.controller;


import com.drrr.alarm.service.impl.ExternalWebPushTestService;
import com.drrr.infra.notifications.kafka.webpush.WebPushProducer;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WebPushTestController {
    private final ExternalWebPushTestService externalWebPushTestService;
    @Operation(summary = "프론트에서 웹푸시 날려볼 수 있게 만든 웹푸시 API - [JWT 토큰 Required] ",
            description = "호출 성공 시 테스트 웹푸시를 날림")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 웹푸시를 날림")
    })
    @PostMapping("/members/me/web-push/test")
    public void sendTestWebPush(@MemberId final Long memberId, @RequestBody final List<Long> postIds, @RequestParam final
                                LocalDate date) {
        externalWebPushTestService.sendTestWebPush(memberId, postIds,date);
    }
}
