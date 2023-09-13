package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporalTechPostTagRepository extends JpaRepository<TemporalTechPostTag, Long> {
    boolean existsByCategoryAndTemporalTechBlogPost(Category category, TemporalTechBlogPost temporalTechBlogPost);
}
