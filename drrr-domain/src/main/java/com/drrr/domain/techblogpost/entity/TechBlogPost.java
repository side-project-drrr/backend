package com.drrr.domain.techblogpost.entity;

import com.drrr.core.code.TechBlogCode;
import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRRR_TECH_BLOG_POST")
@PrimaryKeyJoinColumn(name = "TECH_BLOG_POST_ID")
public class TechBlogPost extends BaseEntity {
    @Column(nullable = false)
    private LocalDate createdDate;

    // naver의 경우 author가 없는 경우가 있음
    @Column
    private String author;
    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String title;

    // 설명이 없는 기술블로그가 있을 수 있음
    @Column(length = 1000)
    private String summary;

    @Column(nullable = false)
    private String urlSuffix;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    private TechBlogCode techBlogCode;


    @Builder
    private TechBlogPost(LocalDate createdDate, String author, String thumbnailUrl, String title, String summary, String urlSuffix, String url,
                         TechBlogCode crawlerGroup) {
        this.createdDate = createdDate;
        this.author = author;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.summary = summary;
        this.urlSuffix = urlSuffix;
        this.url = url;
        this.techBlogCode = crawlerGroup;
    }

    public static TechBlogPost from(TemporalTechBlogPost temporalTechBlogEntity) {
        return TechBlogPost.builder()
                .title(temporalTechBlogEntity.getTitle())
                .author(temporalTechBlogEntity.getAuthor())
                .crawlerGroup(temporalTechBlogEntity.getTechBlogCode())
                .createdDate(temporalTechBlogEntity.getCreatedDate())
                .url(temporalTechBlogEntity.getUrl())
                .summary(temporalTechBlogEntity.getSummary())
                .urlSuffix(temporalTechBlogEntity.getUrlSuffix())
                .build();
    }
}

