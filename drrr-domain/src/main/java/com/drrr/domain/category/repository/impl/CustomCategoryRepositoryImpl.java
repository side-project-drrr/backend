package com.drrr.domain.category.repository.impl;

import static com.drrr.domain.category.entity.QCategory.category;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CustomCategoryRepository;
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
    public List<Category> findByUniqueNameOrDisplayNameContaining(final String text, final Pageable pageable) {
        final BooleanExpression uniqueNameOrDisplayNameSearchCondition = category.displayName.startsWith(text).or(
                category.uniqueName.startsWith(text)
        );

        return queryFactory.select(category)
                .from(category)
                .where(uniqueNameOrDisplayNameSearchCondition)
                .limit(pageable.getPageSize())
                .orderBy(category.uniqueName.asc())
                .fetch();
    }
}
