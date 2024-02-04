package com.drrr.infra.push.repository;

import com.drrr.infra.push.entity.PushStatus;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PushStatusRepository extends JpaRepository<PushStatus, Long> {
    @Modifying
    @Query("UPDATE PushStatus ps set ps.status = true where ps.memberId = :memberId and ps.pushDate = :pushDate")
    int updatePushStatus(@Param("memberId") final Long memberId, @Param("pushDate") final LocalDate pushDate);
}
