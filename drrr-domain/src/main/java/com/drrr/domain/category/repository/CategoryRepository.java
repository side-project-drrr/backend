package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.Category;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {
    List<Category> findByIdIn(Set<Long> categoryIds);

    List<Category> findByIdIn(List<Long> categoryIds);
}
