package com.drrr.domain.log.repository;

import com.drrr.domain.log.entity.post.MemberPostLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPostLogRepository extends JpaRepository<MemberPostLog, Long>, CustomMemberPostLogRepository {
    Optional<MemberPostLog> findByPostId(Long postId);

    List<MemberPostLog> findByMemberId(Long memberId);

    @Query("select mpl from MemberPostLog mpl where mpl.memberId =:memberId and mpl.postId =:postId")
    Optional<MemberPostLog> findByPostIdAndMemberId(@Param("memberId") Long memberId, @Param("postId") Long postId);

}
