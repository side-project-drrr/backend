package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.CategoryWeight;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryWeightRepository extends JpaRepository<CategoryWeight, Long>, CustomCategoryWeightRepository {

    List<CategoryWeight> findByMemberId(final Long memberId);

    @Query("select cw.category.id from CategoryWeight cw where cw.member.id = :memberId")
    List<Long> findCategoryIdsByMemberId(@Param("memberId") final Long memberId);


    @Modifying
    void deleteByMemberId(final Long memberId);

}
