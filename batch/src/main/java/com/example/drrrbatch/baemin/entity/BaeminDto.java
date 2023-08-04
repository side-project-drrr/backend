package com.example.drrrbatch.baemin.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaeminDto {
    private Long id;
    private String date;
    private String author;
    private String thumbnailUrl;
    private String title;
    private String summary;
    private String postId;
    private String url;
    private String catalogues;
}
