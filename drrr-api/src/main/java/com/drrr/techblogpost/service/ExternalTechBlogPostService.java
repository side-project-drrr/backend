package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.web.page.request.PageableRequest;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    private final RedisTechBlogPostService redisTechBlogPostService;
    private final TechBlogPostRepository techBlogPostRepository;

    public Slice<TechBlogPostCategoryDto> execute(final PageableRequest pageableRequest) {
        return techBlogPostRepository.findAllPosts(pageableRequest.fromPageRequest());
    }

    public Slice<TechBlogPostCategoryDto> execute(final Long categoryId, final PageableRequest pageableRequest) {
        return techBlogPostRepository.findPostsByCategory(categoryId, pageableRequest.fromPageRequest());
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
