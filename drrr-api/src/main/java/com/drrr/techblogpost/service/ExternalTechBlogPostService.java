package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.techblogpost.dto.TechBlogPostInnerDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostOuterDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.techblogpost.dto.TechBlogPostLikeDto;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    private final TechBlogPostLikeService techBlogPostLikeService;
    private final RedisTechBlogPostService redisTechBlogPostService;

    public List<TechBlogPostOuterDto> execute() {
        return techBlogPostService.findAllPostsOuter();
    }

    public List<TechBlogPostOuterDto> execute(final Long categoryId) {
        final List<TechBlogPost> postsByCategoryIdInRedis = redisTechBlogPostService.findPostsByCategoryIdInRedis(
                categoryId);

        //redis에서 가져온 데이터가 null인 경우
        if (Objects.isNull(postsByCategoryIdInRedis)) {
            List<TechBlogPost> postsByCategory = techBlogPostService.findPostsByCategory(categoryId);
            redisTechBlogPostService.saveCategoryPostsInRedis(categoryId, postsByCategory);
            return TechBlogPostOuterDto.from(postsByCategory);
        }
        return TechBlogPostOuterDto.from(postsByCategoryIdInRedis);
    }

    public void execute(final TechBlogPostLikeDto request, final String type) {
        if ("ADD".equals(type)) {
            techBlogPostLikeService.addPostLike(request.memberId(), request.postId());
        } else {
            techBlogPostLikeService.deletePostLike(request.memberId(), request.postId());
        }
    }

    public TechBlogPostInnerDto executeFindPostDetail(final Long postId) {
        TechBlogPost postByIdInRedis = redisTechBlogPostService.findPostByIdInRedis(postId);

        if (!Objects.isNull(postByIdInRedis)) {
            return TechBlogPostInnerDto.from(postByIdInRedis);
        }

        TechBlogPost post = techBlogPostService.findTechBlogPostsById(postId);
        return TechBlogPostInnerDto.from(post);
    }
}
