package com.drrr.domain.techblogpost.repository;


import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporalTechBlogPostRepository extends JpaRepository<TemporalTechBlogPost, Long>, CustomTemporalTechBlogPostRepository {

}