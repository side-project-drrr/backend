package com.drrr.domain.category.service;

import com.drrr.domain.category.domain.Categories;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.service.SearchMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class InitializeWeightService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final CategoryRepository categoryRepository;
    private final SearchMemberService searchMemberService;

    public void initializeCategoryWeight(final Long memberId, final List<Long> categories) {
        Categories categoryList = new Categories(categoryRepository.findIds(categories));
        Member member = searchMemberService.execute(memberId);

        categoryWeightRepository.saveAll(categoryList.toCategoryWeights(member));
    }


}
