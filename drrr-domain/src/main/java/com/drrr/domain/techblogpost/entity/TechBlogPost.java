package com.drrr.domain.techblogpost.entity;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.jpa.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRRR_TECHBLOGPOST")
@PrimaryKeyJoinColumn(name = "TECH_BLOG_POST_ID")
public class TechBlogPost extends BaseEntity {

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

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @Column(nullable = false)
    private String urlSuffix;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    private TechBlogCode techBlogCode;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private int postLike = 0;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @Column(nullable = false)
    private LocalDate writtenAt;


    @Builder
    public TechBlogPost(LocalDate writtenAt, String author, String thumbnailUrl, String title, String summary,
                        String aiSummary,
                        String urlSuffix, String url,
                        TechBlogCode crawlerGroup, int viewCount, int like) {
        this.writtenAt = writtenAt;
        this.author = author;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.summary = summary;
        this.aiSummary = aiSummary;
        this.urlSuffix = urlSuffix;
        this.url = url;
        this.techBlogCode = crawlerGroup;
        this.viewCount = viewCount;
        this.postLike = like;
    }

    public static TechBlogPost from(TemporalTechBlogPost temporalTechBlogEntity) {
        return TechBlogPost.builder()
                .title(temporalTechBlogEntity.getTitle())
                .author(temporalTechBlogEntity.getAuthor())
                .crawlerGroup(temporalTechBlogEntity.getTechBlogCode())
                .writtenAt(temporalTechBlogEntity.getWrittenAt())
                .thumbnailUrl(temporalTechBlogEntity.getThumbnailUrl())
                .url(temporalTechBlogEntity.getUrl())
                .summary(temporalTechBlogEntity.getSummary())
                .aiSummary(temporalTechBlogEntity.getAiSummary())
                .urlSuffix(temporalTechBlogEntity.getUrlSuffix())
                .build();
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void increaseLikeCount() {
        this.postLike += 1;
    }

    public void decreaseLikeCount() {
        this.postLike -= 1;
    }
}

