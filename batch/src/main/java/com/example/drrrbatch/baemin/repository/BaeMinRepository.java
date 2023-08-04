package com.example.drrrbatch.baemin.repository;

import com.example.drrrbatch.baemin.entity.BaeMinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface BaeMinRepository extends JpaRepository<BaeMinEntity, Long> {
    BaeMinEntity findByPostId(@Param("postId") String postId);
}
