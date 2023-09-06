package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporalTechPostTagRepository extends JpaRepository<TemporalTechPostTag, Long> {
}
