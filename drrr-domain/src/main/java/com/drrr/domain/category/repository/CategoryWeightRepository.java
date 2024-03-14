package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.CategoryWeight;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryWeightRepository extends JpaRepository<CategoryWeight, Long> {

    List<CategoryWeight> findByMemberId(final Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cw from CategoryWeight cw where cw.member.id = :memberId and cw.category.id in :categoryIds")
    List<CategoryWeight> findByMemberIdAndCategoryIdsWithPessimisticLock(@Param("memberId") final Long memberId,
                                                                         @Param("categoryIds") final List<Long> categoryIds);

    @Modifying
    void deleteByMemberId(final Long memberId);

}
