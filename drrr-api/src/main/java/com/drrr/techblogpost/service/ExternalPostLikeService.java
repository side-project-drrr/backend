package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.techblogpost.event.RedisPostsEventListener.IncreaseLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalPostLikeService {
    private final TechBlogPostLikeService techBlogPostLikeService;
    private final ApplicationEventPublisher publisher;

    public void execute(final Long memberId, final Long postId) {
        techBlogPostLikeService.addPostLike(memberId, postId);
        publisher.publishEvent(new IncreaseLikeEvent(postId));
    }
}
