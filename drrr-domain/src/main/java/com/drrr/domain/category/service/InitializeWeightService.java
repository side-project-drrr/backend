package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public void initializeCategoryWeight(final Long memberId, final List<Long> categories) {
        List<Category> categoryList = categoryRepository.findIds(categories);

        if (categories.isEmpty()) {
            log.error("InitializeWeightService Class initializeCategoryWeight(final Long memberId, final List<Long> categories) Method RuntimeException Error");
            throw new RuntimeException(
                    "InitializeWeightService.initializeCategoryWeight() - Cannot find such categories");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("initializeCategoryWeight(final Long memberId, final List<Long> categories) Method NoSuchElementException Error");
                    return new NoSuchElementException(
                            "InitializeWeightService.initializeCategoryWeight() - No Member Weight found with memberId: "
                                    + memberId);
                });
        List<CategoryWeight> initialCategoryWeight = categoryList.stream()
                .map(category -> CategoryWeight.builder()
                        .member(member)
                        .category(category)
                        .value(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                        .preferred(true)
                        .build()).toList();

        categoryWeightRepository.saveAll(initialCategoryWeight);
    }


}
