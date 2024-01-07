package com.drrr.domain.category.service;

import com.drrr.core.exception.member.MemberExceptionCode;
import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public void initializeCategoryWeight(final Long memberId, final List<Long> categories) {
        List<Category> categoryList = categoryRepository.findIds(categories);

        if (categoryList.isEmpty()) {
            log.error("카테고리 ids 중 존재하는 않는 카테고리가 있습니다 -> {}", categories);
            throw new RuntimeException(
                    "InitializeWeightService.initializeCategoryWeight() - Cannot find such categories -> "
                            + categories);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다.");
                    log.error("memberId -> {}", memberId);
                    return MemberExceptionCode.MEMBER_NOT_FOUND.newInstance();
                });

        List<CategoryWeight> initialCategoryWeight = categoryList.stream()
                .map(category -> CategoryWeight.builder()
                        .member(member)
                        .category(category)
                        .weightValue(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                        .preferred(true)
                        .build()).toList();

        categoryWeightRepository.saveAll(initialCategoryWeight);
    }


}
