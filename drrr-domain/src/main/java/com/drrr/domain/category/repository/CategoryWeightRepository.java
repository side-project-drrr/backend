package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.CategoryWeight;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryWeightRepository extends JpaRepository<CategoryWeight, Long> {

    List<CategoryWeight> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

}
