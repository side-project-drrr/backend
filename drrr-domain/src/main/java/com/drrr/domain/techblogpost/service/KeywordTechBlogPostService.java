package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostSliceDto;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class KeywordTechBlogPostService {
    private final TechBlogPostRepository techBlogPostRepository;

    public TechBlogPostSliceDto findPostsByKeyword(final String keyword, final int page, final int size) {
        return techBlogPostRepository.searchPostsTitleByKeyword(
                keyword, PageRequest.of(page, size));

    }
}
