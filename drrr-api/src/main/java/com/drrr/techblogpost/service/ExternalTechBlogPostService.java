package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.techblogpost.dto.TechBlogPostLikeDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    private final TechBlogPostLikeService techBlogPostLikeService;
    private final RedisTechBlogPostService redisTechBlogPostService;

    public List<TechBlogPost> execute() {
        return techBlogPostService.findAllPosts();
    }

    public List<TechBlogPost> execute(final Long categoryId) {
        final List<TechBlogPost> postsByCategoryIdInRedis = redisTechBlogPostService.findPostsByCategoryIdInRedis(
                categoryId);

        if (postsByCategoryIdInRedis.isEmpty()) {
            List<TechBlogPost> postsByCategory = techBlogPostService.findPostsByCategory(categoryId);
            redisTechBlogPostService.saveCategoryPostsInRedis(categoryId, postsByCategory);
            return techBlogPostService.findPostsByCategory(categoryId);
        }
        return postsByCategoryIdInRedis;
    }

    public void execute(final TechBlogPostLikeDto request, final String type) {
        if ("ADD".equals(type)) {
            techBlogPostLikeService.addPostLike(request.memberId(), request.postId());
        } else {
            techBlogPostLikeService.deletePostLike(request.memberId(), request.postId());
        }
    }
}
