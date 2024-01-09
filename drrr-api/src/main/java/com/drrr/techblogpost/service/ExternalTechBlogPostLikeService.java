package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostOuterDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostLikeService {
    private final TechBlogPostService techBlogPostService;

    public List<TechBlogPostOuterDto> execute(final int count) {

        return techBlogPostService.findTopLikePost(count);
    }
}
