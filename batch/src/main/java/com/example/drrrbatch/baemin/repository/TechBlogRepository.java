package com.example.drrrbatch.baemin.repository;


import com.example.drrrbatch.baemin.code.TechBlogCode;
import com.example.drrrbatch.baemin.entity.TechBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TechBlogRepository extends JpaRepository<TechBlog, Long> {

    @Query("select tb.urlSuffix from TechBlog tb where tb.techBlogCode =:code")
    List<String> findUrlSuffixByCode(@Param("code") TechBlogCode code);

    @Modifying
    @Query("delete from TechBlog tb where tb.urlSuffix = :suffix")
    void deleteBySuffix(@Param("suffix") String suffix);

}
