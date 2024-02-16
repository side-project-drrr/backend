package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalPostDislikeService {
    private final TechBlogPostLikeService techBlogPostLikeService;

    public void execute(final Long memberId, final Long postId) {
        techBlogPostLikeService.deletePostLike(memberId, postId);
    }
}
