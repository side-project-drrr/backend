package com.drrr.domain.techblogpost.entity;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.service.RecommendPostService.ExtractedPostCategoryDto;
import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "DRRR_TECHBLOGPOST_CATEGORY")
@PrimaryKeyJoinColumn(name = "TECHBLOGPOST_CATEGORY_ID")
public class TechBlogPostCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techblogpost_id")
    private TechBlogPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    /**
     * 하나의 게시물에 대한 여러개의 카테고리를 객체로 각각 저장 techBlogPosts
     */
    public static Map<Long, Set<Long>> classifyPostWithCategoriesByMap(List<ExtractedPostCategoryDto> techBlogPosts) {

        Map<Long, Set<Long>> classifiedPostsMap = techBlogPosts.stream()
                .collect(Collectors.groupingBy(
                        ExtractedPostCategoryDto::postId,
                        LinkedHashMap::new,
                        Collectors.mapping(ExtractedPostCategoryDto::categoryId, Collectors.toSet())
                ));
        return classifiedPostsMap;

    }
}
