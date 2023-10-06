package com.drrr.domain.log.repository;

import com.drrr.domain.log.entity.post.MemberPostLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPostLogRepository extends JpaRepository<MemberPostLog, Long> {
    Optional<MemberPostLog> findByPostId(Long postId);

    List<MemberPostLog> findByMemberId(Long memberId);

}
