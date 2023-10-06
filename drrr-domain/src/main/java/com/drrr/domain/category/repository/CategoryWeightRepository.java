package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.CategoryWeight;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryWeightRepository extends JpaRepository<CategoryWeight, Long> {
    @Query("select cw from CategoryWeight cw where cw.member.id =:memberId ")
    List<CategoryWeight> findCategoryWeightsByMemberId(@Param("memberId") Long memberId);
    List<CategoryWeight> findCategoryWeightByMemberId(Long memberId);
    List<CategoryWeight> findByMemberId(Long memberId);
}
