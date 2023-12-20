package com.drrr.temporarytechblogpost.controller;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.techblogpost.service.RegisterPostTagService;
import com.drrr.domain.techblogpost.service.SearchTemporaryTechBlogPostService;
import com.drrr.domain.techblogpost.service.SearchTemporaryTechBlogPostService.SearchTemporaryTechBlogPostDto;
import com.drrr.domain.techblogpost.service.SearchTemporaryTechBlogPostService.SearchTemporaryTechBlogPostResultDto;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/temporary-tech-blog-post")
public class TemporaryTechBlogPostApi {
    private final SearchTemporaryTechBlogPostService searchTemporaryTechBlogPostService;
    private final RegisterPostTagService registerPostTagService;

    @GetMapping
    public SearchTemporalTechBlogPostResponse findTemporalTechBlogPost(
            @ModelAttribute @Valid SearchTemporalTechBlogPostRequest searchTemporalTechBlogPostRequest,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        log.info("{}", searchTemporalTechBlogPostRequest);
        return new SearchTemporalTechBlogPostResponse(searchTemporaryTechBlogPostService.execute(
                SearchTemporaryTechBlogPostDto.builder()
                        .title(searchTemporalTechBlogPostRequest.title())
                        .code(searchTemporalTechBlogPostRequest.getTechBlogCode())
                        .dateRangeBound(searchTemporalTechBlogPostRequest.generateDateRangeBound())
                        .assignTagCompleted(searchTemporalTechBlogPostRequest.assignTagCompleted())
                        .build(),
                pageable
        ));
    }

    @PatchMapping("/{id}/category")
    public void registerPostTag(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid RegisterPostTagRequest registerPostTagRequest) {

        registerPostTagService.execute(id, registerPostTagRequest.tagNames());
    }

    public record RegisterPostTagRequest(
            @NotNull String aiSummarizedText,
            @NotEmpty List<String> tagNames
    ) {
    }

    public record SearchTemporalTechBlogPostResponse(
            Page<SearchTemporaryTechBlogPostResultDto> temporalTechBlogPosts
    ) {

    }

    public record SearchTemporalTechBlogPostRequest(
            @Nullable String title,
            @Nullable Long code,
            @Nullable LocalDate startDate,
            @Nullable LocalDate lastDate,
            @Nullable Boolean assignTagCompleted
    ) {
        public DateRangeBound generateDateRangeBound() {
            // 두 날짜가 모두 비어 있으면 null로 반환함
            if (startDate == null && lastDate == null) {
                return null;
            }
            return DateRangeBound.builder()
                    .startDate(startDate)
                    .lastDate(lastDate)
                    .build();
        }

        public TechBlogCode getTechBlogCode() {
            if (Objects.isNull(code)) {
                return null;
            }
            return TechBlogCode.valueOf(code);
        }
    }


}