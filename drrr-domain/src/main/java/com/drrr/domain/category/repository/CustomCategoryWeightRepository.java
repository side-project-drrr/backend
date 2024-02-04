package com.drrr.domain.category.repository;


import com.drrr.domain.category.dto.PushPostDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomCategoryWeightRepository {
    Slice<PushPostDto> findMemberIdsByCategoryWeights(final Pageable pageable);

}
