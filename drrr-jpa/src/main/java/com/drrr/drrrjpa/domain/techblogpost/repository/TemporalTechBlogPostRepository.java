package com.drrr.drrrjpa.domain.techblogpost.repository;

import com.drrr.drrrjpa.domain.techblogpost.entity.TemporalTechBlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporalTechBlogPostRepository extends JpaRepository<TemporalTechBlogPost, Long> {


}