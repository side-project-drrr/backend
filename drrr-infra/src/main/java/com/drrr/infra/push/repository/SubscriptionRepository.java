package com.drrr.infra.push.repository;


import com.drrr.infra.push.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    void deleteByMemberId(Long memberId);

    List<Subscription> findByMemberIdIn(final List<Long> memberIds);
}
