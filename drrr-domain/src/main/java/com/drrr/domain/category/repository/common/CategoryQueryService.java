package com.drrr.domain.category.repository.common;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryQueryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getCategoriesByIds(final List<Long> categoryIds) {
        final List<Category> categories = categoryRepository.findByIdIn(categoryIds);

        if (categories.size() == 0) {
            log.error("카테고리를 찾을 수 없습니다 => ", categoryIds);
            throw DomainExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }
        return categories;
    }

}