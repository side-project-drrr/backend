package com.drrr.infra.push.repository;

import com.drrr.infra.push.entity.PushPost;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PushPostRepository extends JpaRepository<PushPost, Long> {
    @Query("""
                      select pp.postId 
                        from PushPost pp 
            right outer join PushStatus ps
                          on ps.id = pp.pushStatus.id 
                       where ps.memberId = :memberId AND ps.pushDate = :pushDate 
                      """)
    List<Long> findPostIdByMemberIdAndPushDate(@Param("memberId") final Long memberId,
                                               @Param("pushDate") final LocalDate pushDate);
}
