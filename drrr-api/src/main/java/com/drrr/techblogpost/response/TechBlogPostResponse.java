package com.drrr.techblogpost.response;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostSliceDto;
import java.util.List;

import com.drrr.domain.techblogpost.dto.TechBlogPostViewDto;
import java.util.Set;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@Builder
public record TechBlogPostResponse(
        TechBlogPostViewDto techBlogPostBasicInfoDto,
        List<CategoryDto> categoryDto,
        boolean hasMemberLikedPost
) {
    public static Slice<TechBlogPostResponse> from(
            final List<TechBlogPostCategoryDto> contents,
            final boolean hasNext,
            final Pageable pageable,
            final Set<Long> postIdSet
    ) {
        List<TechBlogPostResponse> techBlogPostResponses = createTechBlogPostResponse(contents, postIdSet);
        return new SliceImpl<>(techBlogPostResponses, pageable, hasNext);
    }

    public static Slice<TechBlogPostResponse> from(final TechBlogPostSliceDto techBlogPostSliceDto, final Set<Long> postIdSet) {

        return new SliceImpl<>(TechBlogPostResponse.from(techBlogPostSliceDto.contents(), postIdSet)
                , techBlogPostSliceDto.pageable(),
                techBlogPostSliceDto.hasNext());
    }

    public static List<TechBlogPostResponse> from(final List<TechBlogPostCategoryDto> contents, final Set<Long> postIdSet) {
        return createTechBlogPostResponse(contents, postIdSet);
    }

    public static List<TechBlogPostResponse> fromRedis(final List<RedisSlicePostsContents> contents, final Set<Long> memberLikedPostIdSet) {
        return contents.stream()
                .map((redisEntity) -> TechBlogPostResponse.builder()
                        .techBlogPostBasicInfoDto(TechBlogPostViewDto.builder()
                        .id(redisEntity.redisTechBlogPostStaticData().id())
                        .title(redisEntity.redisTechBlogPostStaticData().title())
                        .summary(redisEntity.redisTechBlogPostStaticData().summary())
                        .techBlogCode(redisEntity.redisTechBlogPostStaticData().techBlogCode())
                        .thumbnailUrl(redisEntity.redisTechBlogPostStaticData().thumbnailUrl())
                        .writtenAt(redisEntity.redisTechBlogPostStaticData().writtenAt())
                        .url(redisEntity.redisTechBlogPostStaticData().url())
                        .viewCount(redisEntity.redisTechBlogPostDynamicData().getViewCount())
                        .likeCount(redisEntity.redisTechBlogPostDynamicData().getLikeCount()).build())
                        .hasMemberLikedPost(memberLikedPostIdSet.contains(redisEntity.redisTechBlogPostStaticData().id()))
                        .categoryDto(redisEntity.redisCategories().stream()
                                .map(redisCategory -> CategoryDto.builder()
                                        .id(redisCategory.id())
                                        .name(redisCategory.name())
                                        .build())
                                .toList()).build()).toList();
    }

    private static List<TechBlogPostResponse> createTechBlogPostResponse(final List<TechBlogPostCategoryDto> contents, final Set<Long> postIdSet) {
        return contents.stream()
                .map(content -> TechBlogPostResponse.builder()
                        .techBlogPostBasicInfoDto(TechBlogPostViewDto.builder()
                        .id(content.techBlogPostStaticDataDto().id())
                        .title(content.techBlogPostStaticDataDto().title())
                        .summary(content.techBlogPostStaticDataDto().summary())
                        .techBlogCode(content.techBlogPostStaticDataDto().techBlogCode())
                        .thumbnailUrl(content.techBlogPostStaticDataDto().thumbnailUrl())
                        .writtenAt(content.techBlogPostStaticDataDto().writtenAt())
                        .url(content.techBlogPostStaticDataDto().url())
                        .viewCount(content.techBlogPostDynamicDto().viewCount())
                        .likeCount(content.techBlogPostDynamicDto().likeCount()).build())
                        .hasMemberLikedPost(postIdSet.contains(content.techBlogPostStaticDataDto().id()))
                        .categoryDto(content.categoryDto())
                        .build())
                .toList();
    }


}
