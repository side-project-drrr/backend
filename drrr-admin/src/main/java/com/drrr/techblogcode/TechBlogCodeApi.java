package com.drrr.techblogcode;

import com.drrr.core.code.techblog.TechBlogCode;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tech-blog-code")
public class TechBlogCodeApi {

    @GetMapping
    public List<TechBlogCodeResponse> getTechBlogCodeList() {
        return Arrays.stream(TechBlogCode.values())
                .map(TechBlogCodeResponse::from)
                .toList();
    }

    public record TechBlogCodeResponse(Long id, String name) {
        public static TechBlogCodeResponse from(TechBlogCode techBlogCode) {
            return new TechBlogCodeResponse(techBlogCode.getId(), techBlogCode.getName());
        }

    }

}

