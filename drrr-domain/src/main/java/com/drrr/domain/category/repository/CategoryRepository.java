package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {
    List<Category> findByIdIn(final Set<Long> categoryIds);

    Slice<Category> findBy(final Pageable pageable);

    List<Category> findByIdIn(final List<Long> categoryIds);

    List<Category> findByIdInOrderByName(final List<Long> categoryIds);

    Optional<Category> findByName(final String tagName);

}
