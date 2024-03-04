package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.web.page.request.PageableRequest;
import java.util.Objects;
import java.util.Optional;
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

        Optional<Slice<TechBlogPostCategoryDto>> byRedis = redisTechBlogPostService.findOptionalSlicePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size()
        );

        if (byRedis.isPresent()) {
            return byRedis.get();
        }

        //redis에 저장되어 있는 게 없으면 db에서 탐색
        final Slice<TechBlogPostCategoryDto> allPosts = techBlogPostRepository.findAllPosts(
                pageableRequest.fromPageRequest());

        //redis에 저장
        redisTechBlogPostService.saveAllPostsInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                allPosts.hasNext(),
                allPosts.getContent()
        );

        return allPosts;
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
