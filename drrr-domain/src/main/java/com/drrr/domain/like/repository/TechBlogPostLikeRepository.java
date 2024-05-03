package com.drrr.domain.like.repository;

import com.drrr.domain.like.entity.TechBlogPostLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TechBlogPostLikeRepository extends JpaRepository<TechBlogPostLike, Long> {
    @Query("select tbpl from TechBlogPostLike tbpl where tbpl.member.id =:memberId and tbpl.post.id =:postId ")
    Optional<TechBlogPostLike> findByPostIdAndMemberId(@Param("memberId") final Long memberId,
                                                       @Param("postId") final Long postId);


    @Modifying
    @Query("delete from TechBlogPostLike pl where pl.member.id =:memberId and pl.post.id =:postId ")
    void deleteByMemberIdAndPostId(@Param("memberId") final Long memberId, @Param("postId") final Long postId);

    @Query("select tbpl.post.id from TechBlogPostLike tbpl where tbpl.member.id =:memberId")
    List<Long> findPostIdsByMemberId(@Param("memberId") final Long memberId);

    @Query("select tbpl from TechBlogPostLike tbpl where tbpl.member.id =:memberId and tbpl.post.id in :postIds")
    List<TechBlogPostLike> findByMemberIdAndPostIdIn(Long memberId, List<Long> postIds);

}
