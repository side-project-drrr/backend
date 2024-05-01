package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.techblogpost.event.RedisPostsEventListener.DecreaseLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalPostDislikeService {
    private final TechBlogPostLikeService techBlogPostLikeService;
    private final ApplicationEventPublisher publisher;

    public void execute(final Long memberId, final Long postId) {
        techBlogPostLikeService.deletePostLike(memberId, postId);
        publisher.publishEvent(new DecreaseLikeEvent(postId));
    }
}
