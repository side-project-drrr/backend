package com.drrr.infra.push.repository;

import com.drrr.infra.push.entity.PushStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushStatusRepository extends JpaRepository<PushStatus, Long> {
}
