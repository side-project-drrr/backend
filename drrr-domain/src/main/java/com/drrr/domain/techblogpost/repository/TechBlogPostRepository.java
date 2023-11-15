package com.drrr.domain.techblogpost.repository;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TechBlogPostRepository extends JpaRepository<TechBlogPost, Long>, CustomTechBlogPostRepository {
    boolean existsByTechBlogCodeAndUrlSuffix(TechBlogCode techBlogCode, String urlSuffix);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tbp from TechBlogPost tbp where tbp.id =:id")
    Optional<TechBlogPost> findByIdWithPessimisticLock(@Param("id") Long id);

}