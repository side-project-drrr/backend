package com.drrr.infra.push.repository;

import com.drrr.infra.push.entity.PushPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushPostRepository extends JpaRepository<PushPost, Long> {
}
