package com.drrr.domain.category.repository;


import com.drrr.domain.category.entity.Category;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomCategoryRepository {
    List<Category> findIds(List<Long> ids);

    List<Category> findByNameContaining(String text, Pageable pageable);

    List<Category> findAllOrderByNames();

    List<Category> findTopCategories(final Long count);

}
