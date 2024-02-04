package com.drrr.domain.category.repository;


import java.util.List;

public interface CustomCategoryWeightRepository {
    List<Long> findMemberIdsByCategoryWeights();

}
