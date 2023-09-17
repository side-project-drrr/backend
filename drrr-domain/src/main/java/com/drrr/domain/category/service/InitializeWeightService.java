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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitializeWeightService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public void initializeCategoryWeight(Long memberId, List<Long> categories) {
        List<Category> categoryList = categoryRepository.findByIds(categories).orElseThrow(() -> new RuntimeException(
                "MemberViewWeightService.increaseMemberViewPost - Cannot find such categories"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("No Member Weight found with memberId: " + memberId));
        List<CategoryWeight> categoryWeightDto = categoryList.stream()
                .map(category -> {
                    return CategoryWeight.builder()
                            .member(member)
                            .category(category)
                            .value(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                            .preferred(true)
                            .build();
                }).toList();

        categoryWeightRepository.saveAll(categoryWeightDto);
    }


}
