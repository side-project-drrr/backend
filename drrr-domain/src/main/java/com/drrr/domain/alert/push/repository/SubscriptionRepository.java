package com.drrr.domain.alert.push.repository;


import com.drrr.domain.alert.push.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    void deleteByMemberId(Long memberId);
}
