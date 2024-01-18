package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporalTechPostTagRepository extends JpaRepository<TemporalTechPostTag, Long> {
    List<TemporalTechPostTag> findByTemporalTechBlogPostId(Long temporalTechBlogPostId);
}
