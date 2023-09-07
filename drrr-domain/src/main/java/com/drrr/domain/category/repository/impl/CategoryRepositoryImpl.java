package com.drrr.domain.category.repository.impl;

import static com.drrr.domain.category.entity.QCategory.category;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CustomCategoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CustomCategoryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findIds(List<Long> ids) {
        return queryFactory.select(category)
                .from(category)
                .where(category.id.in(ids))
                .fetch();
    }
}
