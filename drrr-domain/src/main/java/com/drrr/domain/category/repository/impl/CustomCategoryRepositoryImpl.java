package com.drrr.domain.category.repository.impl;

import static com.drrr.domain.category.entity.QCategory.category;
import static com.drrr.domain.category.entity.QCategoryWeight.categoryWeight;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CustomCategoryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
