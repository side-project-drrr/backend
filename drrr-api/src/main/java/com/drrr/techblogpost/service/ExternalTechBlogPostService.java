package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
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
    private final RedisTechBlogPostService redisTechBlogPostService;
    private final TechBlogPostRepository techBlogPostRepository;

    public Slice<TechBlogPostCategoryDto> execute(final TechBlogPostSliceRequest request) {
        final Sort sort = Sort.by(Sort.Direction.fromString(request.direction()), request.sort());
        final PageRequest pageRequest = PageRequest.of(request.page(), request.size(), sort);

        return techBlogPostRepository.findAllPosts(pageRequest);
    }

    public Slice<TechBlogPostCategoryDto> execute(final Long categoryId, final TechBlogPostSliceRequest request) {
        final Sort sort = Sort.by(Sort.Direction.fromString(request.direction()), request.sort());
        final PageRequest pageRequest = PageRequest.of(request.page(), request.size(), sort);

        return techBlogPostRepository.findPostsByCategory(categoryId, pageRequest);
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
