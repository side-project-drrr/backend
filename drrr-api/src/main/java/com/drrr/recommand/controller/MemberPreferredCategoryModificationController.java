package com.drrr.recommand.controller;

import com.drrr.recommand.service.impl.ExternalMemberPreferredCategoryModificationService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/category/modification")
public class MemberPreferredCategoryModificationController {
    private final ExternalMemberPreferredCategoryModificationService modificationService;
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/{memberId}")
    public ResponseEntity<String> modifyCategory(@NonNull @PathVariable(name = "memberId") final Long memberId, @RequestBody @NonNull final List<Long> categoryIds) {
        modificationService.execute(memberId,categoryIds);
        return ResponseEntity.ok().build();
    }
}
