package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalPostLikeService {
    private final TechBlogPostLikeService techBlogPostLikeService;

    public void execute(final Long memberId, final Long postId) {
        techBlogPostLikeService.addPostLike(memberId, postId);
    }
}
