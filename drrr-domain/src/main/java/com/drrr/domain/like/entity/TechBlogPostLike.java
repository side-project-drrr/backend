package com.drrr.domain.like.entity;

import com.drrr.domain.jpa.entity.BaseEntity;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRRR_TECHBLOGPOST_LIKE")
@PrimaryKeyJoinColumn(name = "TECH_BLOG_POST_LIKE_ID")
public class TechBlogPostLike extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techblogpost_id")
    private TechBlogPost post;
}
