package com.drrr.techblogpost.service;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.techblogpost.cache.entity.RedisTopPostCategories;
import com.drrr.domain.techblogpost.cache.entity.RedisTopPostCategories.CompoundTopPostCategoriesId;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.web.redis.RedisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SearchTopTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    private final RedisTechBlogPostService redisTechBlogPostService;

    @Transactional(readOnly = true)
    public List<TechBlogPostCategoryDto> execute(final int count, final TopTechBlogType type) {
        RedisTopPostCategories redisTopPostCategories = redisTechBlogPostService.findCacheTopPostsInRedis(count, type);

        //redis key 값
        final CompoundTopPostCategoriesId key = CompoundTopPostCategoriesId.builder()
                .topTechBlogType(type)
                .count(count)
                .build();

        if (redisTechBlogPostService.hasCachedKey(key)) {
            return RedisUtil.redisPostCategoriesEntityToDto(
                    redisTopPostCategories.redisTechBlogPostCategories());
        }

        List<TechBlogPostCategoryDto> topPostsByType = techBlogPostService.findTopPostByType(count, type);
        //redis에 저장
        redisTechBlogPostService.saveTopPostCategoriesInRedis(type, topPostsByType, count);
        return topPostsByType;
    }

}
