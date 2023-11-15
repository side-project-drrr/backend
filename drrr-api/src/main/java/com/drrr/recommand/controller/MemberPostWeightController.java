package com.drrr.recommand.controller;

import com.drrr.recommand.dto.AdjustPostWeightRequest;
import com.drrr.recommand.service.impl.ExternalMemberPostReadService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class MemberPostWeightController {
    private final ExternalMemberPostReadService memberPostReadService;

    @Operation(summary = "사용자 게시물을 읽을 때 가중치 계산 API", description = "호출 성공 시 가중치 계산 및 로깅")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/read/{memberId}/{postId}")
    public ResponseEntity<String> MemberPostReadController(
            @Validated @RequestBody final AdjustPostWeightRequest request,
            @NonNull @PathVariable(name = "memberId") final Long memberId,
            @NonNull @PathVariable(name = "postId") final Long postId) {
        memberPostReadService.execute(request, memberId, postId);
        return ResponseEntity.ok().build();
    }

}
