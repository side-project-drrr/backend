package com.drrr.infra.push.repository;

import com.drrr.infra.push.entity.PushStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PushStatusRepository extends JpaRepository<PushStatus, Long>, CustomPushStatusRepository {
    @Modifying
    @Query("update PushStatus ps set ps.status = true where ps.memberId = :memberId and ps.pushDate = :pushDate")
    int updatePushStatus(@Param("memberId") final Long memberId, @Param("pushDate") final LocalDate pushDate);

    List<Long> findPostIdsByMemberIdAndPushDate(final Long memberId, final LocalDate pushDate);

    boolean existsPushStatusByMemberIdAndPushDate(final Long memberId, final LocalDate pushDate);
}
