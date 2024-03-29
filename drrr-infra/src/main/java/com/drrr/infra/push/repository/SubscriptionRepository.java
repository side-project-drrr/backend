package com.drrr.infra.push.repository;


import com.drrr.infra.push.entity.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByMemberId(final Long memberId);

    @Modifying
    void deleteByMemberId(final Long memberId);

    @Modifying
    void deleteById(final Long id);

    List<Subscription> findByMemberIdIn(final List<Long> memberIds);

}
