package com.drrr.techblogpost.service;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchTopTechBlogPostService {
    private final TechBlogPostService techBlogPostService;

    public List<TechBlogPostCategoryDto> execute(final int count, final TopTechBlogType type) {
        return techBlogPostService.findTopPostByType(count, type);
    }
}
