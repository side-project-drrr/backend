package com.drrr.domain.techblogpost.repository;

import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import java.util.List;

public interface CustomTemporalTechBlogPostRepository {

    List<TemporalTechBlogPost> findBy(DateRangeBound rangeBound);
}
