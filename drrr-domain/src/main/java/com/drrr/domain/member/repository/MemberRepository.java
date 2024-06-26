package com.drrr.domain.member.repository;

import com.drrr.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    Optional<Member> findByEmail(final String email);

    @Query("select m.nickname from Member m where m.id =:memberId")
    String findNicknameById(@Param("memberId") final Long memberId);

    Optional<Member> findByProviderId(final String providerId);

    boolean existsByEmail(final String email);

    @Query("select count(m.id) > 0 from Member m where m.id !=:memberId and m.email =:email")
    boolean existsByEmailFromOthers(@Param("memberId") final Long memberId, @Param("email") final String email);

    boolean existsByNickname(final String nickname);

    @Query("select m.email from Member m where m.id =:memberId")
    String findEmailByMemberId(final Long memberId);

    @Query("select count(m.id) > 0 from Member m where m.id !=:memberId and m.nickname =:nickname")
    boolean existsByNicknameFromOthers(@Param("memberId") final Long memberId,
                                       @Param("nickname") final String nickname);

    boolean existsByProviderId(final String providerId);

    @Query("select m.isActive from Member m where m.id =:memberId")
    boolean findActiveByMemberId(@Param("memberId") final Long memberId);

    @Modifying
    @Query("update Member m set m.isActive = false where m.id =:memberId ")
    void updateUnregisterMember(@Param("memberId") final Long memberId);

    @Modifying
    @Query("update Member m set m.email =:email where m.id =:memberId")
    void updateEmail(@Param("memberId") final Long memberId, @Param("email") final String email);


}
