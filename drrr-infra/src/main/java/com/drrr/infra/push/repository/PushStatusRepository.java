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
    @Query("update PushStatus ps set ps.openStatus = true where ps.memberId = :memberId and ps.pushDate in :pushDates")
    int updatePushOpenStatus(@Param("memberId") final Long memberId,
                             @Param("pushDates") final List<LocalDate> pushDates);

    @Modifying
    @Query("update PushStatus ps set ps.readStatus = true where ps.memberId = :memberId and ps.pushDate = :pushDate")
    int updatePushReadStatus(@Param("memberId") final Long memberId, @Param("pushDate") final LocalDate pushDate);

    @Query("select ps.postIds from PushStatus ps where ps.memberId = :memberId and ps.pushDate = :pushDate")
    List<Long> findPostIdsByMemberIdAndPushDate(@Param("memberId") final Long memberId,
                                                @Param("pushDate") final LocalDate pushDate);

    @Query("select ps.postIds from PushStatus ps where ps.memberId = :memberId and ps.pushDate between :from and :to")
    List<Long> findPostIdsByMemberIdAndPushDateRange(@Param("memberId") final Long memberId,
                                                     @Param("from") final LocalDate from,
                                                     @Param("to") final LocalDate to);

    boolean existsPushStatusByMemberIdAndPushDate(final Long memberId, final LocalDate pushDate);
}
