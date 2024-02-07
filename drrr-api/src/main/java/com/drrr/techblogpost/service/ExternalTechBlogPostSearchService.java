package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.techblogpost.dto.TechBlogPostIndexSliceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ExternalTechBlogPostSearchService {
    private final TechBlogPostRepository techBlogPostRepository;

    public Slice<TechBlogPostCategoryDto> execute(final TechBlogPostIndexSliceRequest request) {
        final Sort sort = Sort.by(Sort.Direction.fromString(request.direction()), request.sort());
        final PageRequest pageRequest = PageRequest.of(request.page(), request.size(), sort);
        return techBlogPostRepository.searchPostsTitleByWord(request.index(), pageRequest);
    }
}
