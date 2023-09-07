package com.drrr.domain.category.repository;


import com.drrr.domain.category.entity.Category;
import java.util.List;

public interface CustomCategoryRepository {
    List<Category> findIds(List<Long> ids);
}
