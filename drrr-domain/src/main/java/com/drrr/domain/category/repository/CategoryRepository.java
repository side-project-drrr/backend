package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.id IN (:categoryIds)")
    Optional<List<Category>> findByIds(List<Long> categoryIds);
}
