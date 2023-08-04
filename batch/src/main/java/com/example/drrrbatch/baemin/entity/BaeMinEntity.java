package com.example.drrrbatch.baemin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BaeMinEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String date;
    private String author;
    @Column(length = 500)
    private String thumbnailUrl;
    private String title;
    private String summary;
    private String postId;
    private String url;
    private String catalogues;
}
