package com.example.drrrbatch.baemin.entity;

import com.example.drrrbatch.baemin.code.TechBlogCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TechBlog {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String author;
    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private String urlSuffix;

    @Column(nullable = false)
    private String url;
    @Enumerated(EnumType.STRING)
    private TechBlogCode techBlogCode;


}
