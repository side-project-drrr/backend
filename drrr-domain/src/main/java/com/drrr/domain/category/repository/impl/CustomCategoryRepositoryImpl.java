package com.drrr.domain.category.repository.impl;

import static com.drrr.domain.category.entity.QCategory.category;
import static com.drrr.domain.category.entity.QCategoryWeight.categoryWeight;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryPostDto;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CustomCategoryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findIds(List<Long> ids) {
        return queryFactory.select(category)
                .from(category)
                .where(category.id.in(ids))
                .fetch();
    }

    @Override
    public List<CategoryDto> findCategoriesByMemberId(Long memberId) {
        return queryFactory.select(Projections.constructor(CategoryDto.class, category.id, category.name))
                .from(category)
                .innerJoin(categoryWeight)
                .on(category.id.eq(categoryWeight.category.id), categoryWeight.member.id.eq(memberId),
                        categoryWeight.preferred.eq(true))
                .orderBy(category.name.asc())
                .fetch();
    }

    @Override
    public List<CategoryDto> findCategoriesByPostId(final Long postId) {
        return queryFactory.select(Projections.constructor(CategoryDto.class, category.id, category.name))
                .from(category)
                .innerJoin(techBlogPostCategory)
                .on(category.id.eq(techBlogPostCategory.category.id), techBlogPostCategory.post.id.eq(postId))
                .orderBy(category.name.asc())
                .fetch();
    }

    private BooleanExpression languageCondition(final LanguageConstants language,
                                                IndexConstants indexConstants) {
        if (language.equals(LanguageConstants.ENGLISH)) {
            return category.name.like(
                    indexConstants.getCharacter() + "%");
        }
        return category.name.goe(indexConstants.getCharacter()).
                and(category.name.lt(indexConstants.getNext().getCharacter()));
    }

    @Override
    public Slice<CategoryDto> findCategoryByNameLike(final LanguageConstants language,
                                                     IndexConstants indexConstants,
                                                     Pageable pageable) {

        List<CategoryDto> content = queryFactory.select(
                        Projections.constructor(CategoryDto.class
                                , category.id
                                , category.name))
                .from(category)
                .where(languageCondition(language, indexConstants))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(category.count())
                .from(category)
                .where(languageCondition(language, indexConstants))
                .fetchOne();

        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<CategoryPostDto> findEachPostCategoriesByPostIds(final List<Long> postId) {
        return queryFactory.select(Projections.constructor(CategoryPostDto.class
                        , category.id
                        , techBlogPostCategory.post.id
                        , category.name))
                .from(category)
                .innerJoin(techBlogPostCategory)
                .on(category.id.eq(techBlogPostCategory.category.id))
                .where(techBlogPostCategory.post.id.in(postId))
                .fetch();
    }

    @Override
    public Slice<CategoryDto> searchCategoriesByKeyWord(String keyword, Pageable pageable) {
        List<CategoryDto> content = queryFactory.select(
                        Projections.constructor(CategoryDto.class
                                , category.id
                                , category.name))
                .from(category)
                .where(category.name.like("%" + keyword + "%"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(category.count())
                .from(category)
                .where(category.name.like("%" + keyword + "%"))
                .fetchOne();

        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<Category> findByNameContaining(final String text, final Pageable pageable) {
        final BooleanExpression uniqueNameOrDisplayNameSearchCondition = category.name.startsWith(text);

        return queryFactory.select(category)
                .from(category)
                .where(uniqueNameOrDisplayNameSearchCondition)
                .limit(pageable.getPageSize())
                .orderBy(category.name.asc())
                .fetch();
    }

    @Override
    public List<Category> findTopCategories(Long count) {
        return queryFactory.select(categoryWeight.category)
                .from(categoryWeight)
                .where(categoryWeight.preferred.eq(true))
                .groupBy(categoryWeight.category.id)
                .orderBy(categoryWeight.category.id.count().desc())
                .limit(count)
                .fetch();
    }


}
