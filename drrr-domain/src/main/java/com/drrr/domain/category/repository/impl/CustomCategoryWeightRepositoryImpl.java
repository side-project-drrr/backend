package com.drrr.domain.category.repository.impl;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CustomCategoryRepository;
import com.drrr.domain.category.repository.CustomCategoryWeightRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.drrr.domain.member.entity.QMember.member;
import static com.drrr.domain.category.entity.QCategoryWeight.categoryWeight;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;


@Repository
@RequiredArgsConstructor
public class CustomCategoryWeightRepositoryImpl implements CustomCategoryWeightRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<Long> findMemberIdsByCategoryWeights() {
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')"
                , techBlogPost.createdAt);
        return queryFactory.select(categoryWeight.member.id)
                .from(categoryWeight)
                .where(categoryWeight.category.id
                        .in(
                        queryFactory
                                .select(techBlogPostCategory.category.id)
                                .from(techBlogPost)
                                .leftJoin(techBlogPostCategory)
                                .on(techBlogPost.id.eq(techBlogPostCategory.id)
                                        , formattedDate.eq(LocalDate.now().toString()))
                                .groupBy(techBlogPostCategory.category.id))
                )
                .groupBy(categoryWeight.member.id)
                .fetch();
    }
}
