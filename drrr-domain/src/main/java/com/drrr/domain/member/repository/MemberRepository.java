package com.drrr.domain.member.repository;

import com.drrr.domain.member.entity.Member;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("select m.nickname from Member m where m.id =:id")
    String findNicknameById(@Param("id") Long id);

    Optional<Member> findByProviderId(String providerId);


    boolean existsByProviderId(String providerId);

    @Modifying
    @Query("update Member m set m.isActive = false where m.id =:memberId ")
    void updateUnregisterMember(@Param("memberId") final Long memberId);

}
