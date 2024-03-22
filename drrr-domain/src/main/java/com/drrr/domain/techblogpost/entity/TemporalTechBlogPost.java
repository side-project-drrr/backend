package com.drrr.domain.techblogpost.entity;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "DRRR_TEMP_TECH_BLOG_POST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "TEMP_TECH_BLOG_POST_ID")
public class TemporalTechBlogPost extends BaseEntity {

    @Column(nullable = false)
    private LocalDate writtenAt;

    @Column
    private String author;
    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String title;

    // 설명이 없는 기술블로그가 있음
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


    /**
     * 크롤링 시작 일자. 관리자 앱에서 해당 일자를 기준으로 범위를 제한 함
     */
    @Column(nullable = false)
    private LocalDate crawledDate;

    @Column(nullable = false)
    private boolean registrationCompleted;

    @OneToMany(mappedBy = "temporalTechBlogPost", cascade = CascadeType.REMOVE)
    private List<TemporalTechPostTag> temporalTechPostTags;

    @Builder
    public TemporalTechBlogPost(LocalDate createdDate,
                                String author,
                                String thumbnailUrl,
                                String title,
                                String summary,
                                String aiSummary,
                                String urlSuffix,
                                String url,
                                TechBlogCode techBlogCode,
                                LocalDate crawledDate,
                                boolean registrationCompleted,
                                List<TemporalTechPostTag> temporalTechPostTags
    ) {
        this.writtenAt = createdDate;
        this.author = author;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.summary = summary;
        this.aiSummary = aiSummary;
        this.urlSuffix = urlSuffix;
        this.url = url;
        this.techBlogCode = techBlogCode;

        this.temporalTechPostTags = temporalTechPostTags;

        this.crawledDate = crawledDate;
        this.registrationCompleted = registrationCompleted;
    }

    public void registerCategory(List<TemporalTechPostTag> tags) {
        this.registrationCompleted = true;
        if (this.temporalTechPostTags == null) {
            this.temporalTechPostTags = tags;
            return;
        }
        this.temporalTechPostTags.addAll(tags);
    }

    public void removeCategory(List<TemporalTechPostTag> tags) {
        if (this.temporalTechPostTags == null) {
            return;
        }
        this.temporalTechPostTags.removeAll(tags);
    }

    public void updateAiSummarizedText(String aiSummarizedText) {
        this.aiSummary = aiSummarizedText;
    }

    public void transistorComplete() {
        this.registrationCompleted = true;

    }
}




