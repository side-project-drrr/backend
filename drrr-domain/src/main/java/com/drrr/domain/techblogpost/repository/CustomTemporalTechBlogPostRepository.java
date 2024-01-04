package com.drrr.domain.techblogpost.repository;

import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.service.SearchTemporaryTechBlogPostService.SearchTemporaryTechBlogPostDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomTemporalTechBlogPostRepository {


    List<TemporalTechBlogPost> findBy(DateRangeBound rangeBound, Boolean assignTagCompleted);

    Page<TemporalTechBlogPost> findBy(SearchTemporaryTechBlogPostDto searchTemporaryTechBlogPostDto, Pageable pageable);


}
