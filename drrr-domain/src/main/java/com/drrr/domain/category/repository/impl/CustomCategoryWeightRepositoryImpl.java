package com.drrr.domain.category.repository.impl;

import static com.drrr.domain.category.entity.QCategoryWeight.categoryWeight;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.domain.category.dto.PushPostDto;
import com.drrr.domain.category.repository.CustomCategoryWeightRepository;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CustomCategoryWeightRepositoryImpl implements CustomCategoryWeightRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<PushPostDto> findMemberIdsByCategoryWeights(final Pageable pageable) {
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')"
                , techBlogPost.writtenAt);
        List<Long> postIds = queryFactory
                .select(techBlogPost.id)
                .from(techBlogPost)
                .leftJoin(techBlogPostCategory)
                .on(techBlogPost.id.eq(techBlogPostCategory.id)
                        , formattedDate.eq(String.valueOf(LocalDate.now())))
                .groupBy(techBlogPostCategory.category.id).fetch();

        List<Long> memberIds = queryFactory.select(categoryWeight.member.id)
                .from(categoryWeight)
                .where(categoryWeight.category.id
                        .in(postIds)
                )
                .groupBy(categoryWeight.member.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(categoryWeight.count())
                .from(categoryWeight)
                .where(categoryWeight.category.id
                        .in(postIds)
                )
                .groupBy(categoryWeight.member.id)
                .fetchOne();

        List<PushPostDto> pushPostDtos = memberIds.stream()
                .map(memberId -> PushPostDto.builder()
                        .memberId(memberId)
                        .postIds(postIds)
                        .build()
                ).toList();

        return new PageImpl<>(pushPostDtos, pageable, total);
    }

}
