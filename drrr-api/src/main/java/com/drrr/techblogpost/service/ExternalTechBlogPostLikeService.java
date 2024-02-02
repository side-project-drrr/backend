package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostLikeService {
    private final TechBlogPostService techBlogPostService;

    public List<TechBlogPostBasicInfoDto> execute(final int count) {

        return techBlogPostService.findTopLikePost(count);
    }
}
