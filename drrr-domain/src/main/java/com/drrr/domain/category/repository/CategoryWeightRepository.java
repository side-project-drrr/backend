package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.CategoryWeight;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryWeightRepository extends JpaRepository<CategoryWeight, Long> {
    @Query("select cw from CategoryWeight cw where cw.member.id =:memberId ")
    List<CategoryWeight> findCategoryWeightsByMemberId(@Param("memberId") Long memberId);
    List<CategoryWeight> findCategoryWeightByMemberId(Long memberId);

   /* @Query("delete from CategoryWeight cw where cw.member.id =:memberId and cw.category.id =:categoryId")
    Optional<Integer> deleteUselessCategoryWeightData(Long memberId, Long categoryId);

    @Query("select cw from CategoryWeight cw where cw.member.id =:memberId and cw.category.id in (:categoryIds)")
    Optional<List<CategoryWeight>> findByMemberAndCategory(Long memberId, List<Long> categoryIds);
*/
    List<CategoryWeight> findByMemberId(Long memberId);
}
