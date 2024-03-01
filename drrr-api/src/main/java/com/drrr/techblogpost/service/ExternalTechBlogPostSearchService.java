package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.web.page.request.PageableRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ExternalTechBlogPostSearchService {
    private final TechBlogPostRepository techBlogPostRepository;

    public Slice<TechBlogPostCategoryDto> execute(final String keyword, final PageableRequest pageableRequest) {
        return techBlogPostRepository.searchPostsTitleByKeyword(keyword, pageableRequest.fromPageRequest());
    }
}
