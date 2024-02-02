package com.drrr.techblogpost.service;

import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.techblogpost.dto.TechBlogPostLikeDto;
import com.drrr.techblogpost.request.TechBlogPostSliceRequest;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    private final TechBlogPostLikeService techBlogPostLikeService;
    private final RedisTechBlogPostService redisTechBlogPostService;

    public Slice<TechBlogPostBasicInfoDto> execute(final TechBlogPostSliceRequest request) {
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by(request.direction(), request.sort()));
        return techBlogPostService.findAllPostsOuter(pageRequest);
    }

    public Slice<TechBlogPostBasicInfoDto> execute(final Long categoryId, final TechBlogPostSliceRequest request) {
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by(request.direction(), request.sort()));

        return techBlogPostService.findPostsByCategory(categoryId, pageRequest);
    }

    public void execute(final TechBlogPostLikeDto request, final String type) {
        if ("ADD".equals(type)) {
            techBlogPostLikeService.addPostLike(request.memberId(), request.postId());
        } else {
            techBlogPostLikeService.deletePostLike(request.memberId(), request.postId());
        }
    }

    public TechBlogPostDetailedInfoDto executeFindPostDetail(final Long postId) {
        TechBlogPost postByIdInRedis = redisTechBlogPostService.findPostByIdInRedis(postId);

        if (!Objects.isNull(postByIdInRedis)) {
            return TechBlogPostDetailedInfoDto.from(postByIdInRedis);
        }

        TechBlogPost post = techBlogPostService.findTechBlogPostsById(postId);
        return TechBlogPostDetailedInfoDto.from(post);
    }

}
