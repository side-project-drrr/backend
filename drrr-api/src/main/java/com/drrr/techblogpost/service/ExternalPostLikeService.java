package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.techblogpost.dto.TechBlogPostLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalPostLikeService {
    private final TechBlogPostLikeService techBlogPostLikeService;

    public void execute(final TechBlogPostLikeDto request) {
        techBlogPostLikeService.addPostLike(request.memberId(), request.postId());
    }
}
