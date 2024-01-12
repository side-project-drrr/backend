package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.CategoryWeight;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryWeightRepository extends JpaRepository<CategoryWeight, Long>, CustomCategoryWeightRepository{

    List<CategoryWeight> findByMemberId(Long memberId);

    @Modifying
    void deleteByMemberId(Long memberId);

}
