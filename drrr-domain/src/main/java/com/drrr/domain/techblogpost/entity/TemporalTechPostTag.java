package com.drrr.domain.techblogpost.entity;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 임시 기술블로그 태그 (해시 태그 개념) TemporalTechBlogPostTag 이름이 너무 길어서 TemporalTechPostTag로 줄임
 */


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DRRR_TEMP_POST_TAG")
@PrimaryKeyJoinColumn(name = "TEMP_POST_TAG_ID")
public class TemporalTechPostTag extends BaseEntity {


    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "TEMP_TECH_BLOG_POST_ID")
    private TemporalTechBlogPost temporalTechBlogPost;

    public TemporalTechPostTag(Category category, TemporalTechBlogPost temporalTechBlogPost) {
        this.category = category;
        this.temporalTechBlogPost = temporalTechBlogPost;
    }
}
