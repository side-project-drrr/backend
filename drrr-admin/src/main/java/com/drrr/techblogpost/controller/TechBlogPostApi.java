package com.drrr.techblogpost.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tech-blog-post")
@Slf4j
public record TechBlogPostApi() {

    @GetMapping
    public ResponseEntity<Void> getTechBlogPost(
            @PageableDefault(size = 50) Pageable pageable) {
        log.info("{}", pageable.getPageSize());

        return ResponseEntity.ok().body(null);
    }
}
