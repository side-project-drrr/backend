package com.drrr.code;


import com.drrr.core.code.TechBlogCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/tech-blog-code")
@RequiredArgsConstructor
public class TechBlogCodeController {


    @Operation(description = "기술블로그 코드 전체를 가지고 옵니다.")
    @GetMapping
    public ResponseEntity<List<TechBlogCodeResponse>> findAll() {
        return ResponseEntity.ok(Arrays.stream(TechBlogCode.values())
                .map(techBlogCode -> new TechBlogCodeResponse(techBlogCode.getId(), techBlogCode.getName()))
                .collect(Collectors.toList()));
    }

    public record TechBlogCodeResponse(Long id, String name) {

    }
}
