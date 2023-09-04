package com.drrr.domain.techblogpost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTechBlogPost is a Querydsl query type for TechBlogPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTechBlogPost extends EntityPathBase<TechBlogPost> {

    private static final long serialVersionUID = 1768344036L;

    public static final QTechBlogPost techBlogPost = new QTechBlogPost("techBlogPost");

    public final StringPath author = createString("author");

    public final DatePath<java.time.LocalDate> createdDate = createDate("createdDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath summary = createString("summary");

    public final EnumPath<com.drrr.core.code.TechBlogCode> techBlogCode = createEnum("techBlogCode", com.drrr.core.code.TechBlogCode.class);

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final StringPath url = createString("url");

    public final StringPath urlSuffix = createString("urlSuffix");

    public QTechBlogPost(String variable) {
        super(TechBlogPost.class, forVariable(variable));
    }

    public QTechBlogPost(Path<? extends TechBlogPost> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTechBlogPost(PathMetadata metadata) {
        super(TechBlogPost.class, metadata);
    }

}

