package com.drrr.domain.techblogpost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTemporalTechBlogPost is a Querydsl query type for TemporalTechBlogPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTemporalTechBlogPost extends EntityPathBase<TemporalTechBlogPost> {

    private static final long serialVersionUID = -1590798810L;

    public static final QTemporalTechBlogPost temporalTechBlogPost = new QTemporalTechBlogPost("temporalTechBlogPost");

    public final StringPath author = createString("author");

    public final DatePath<java.time.LocalDate> createdDate = createDate("createdDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath summary = createString("summary");

    public final EnumPath<com.drrr.core.code.TechBlogCode> techBlogCode = createEnum("techBlogCode", com.drrr.core.code.TechBlogCode.class);

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final StringPath url = createString("url");

    public final StringPath urlSuffix = createString("urlSuffix");

    public QTemporalTechBlogPost(String variable) {
        super(TemporalTechBlogPost.class, forVariable(variable));
    }

    public QTemporalTechBlogPost(Path<? extends TemporalTechBlogPost> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTemporalTechBlogPost(PathMetadata metadata) {
        super(TemporalTechBlogPost.class, metadata);
    }

}

