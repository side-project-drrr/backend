package com.drrr.domain.category.repository;


import com.drrr.domain.category.entity.Category;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CustomCategoryWeightRepository {
    List<Long> findMemberIdsByCategoryWeights();

}
