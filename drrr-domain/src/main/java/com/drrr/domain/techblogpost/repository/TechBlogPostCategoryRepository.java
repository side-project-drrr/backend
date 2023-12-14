package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TechBlogPostCategoryRepository extends JpaRepository<TechBlogPostCategory, Long>,
        CustomTechBlogPostCategoryRepository {

    @Query("select tbpc.post.id from TechBlogPostCategory tbpc where tbpc.category.id =:id")
    List<Long> findPostIdsByCategoryId(@Param("id") Long id);
}
