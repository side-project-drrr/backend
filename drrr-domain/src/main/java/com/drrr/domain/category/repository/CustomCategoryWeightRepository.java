package com.drrr.domain.category.repository;


import com.drrr.domain.category.dto.PushPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCategoryWeightRepository {
    Page<PushPostDto> findMemberIdsByCategoryWeights(final Pageable pageable);

}
