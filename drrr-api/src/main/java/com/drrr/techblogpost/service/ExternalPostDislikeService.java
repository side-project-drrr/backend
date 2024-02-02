package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.techblogpost.dto.TechBlogPostLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalPostDislikeService {
    private final TechBlogPostLikeService techBlogPostLikeService;

    public void execute(final TechBlogPostLikeDto request) {
        techBlogPostLikeService.deletePostLike(request.memberId(), request.postId());
    }
}
