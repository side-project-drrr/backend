package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberPreferredCategoryServiceModificationService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public void changeMemberPreferredCategory(final Long memberId, final List<Long> updateCategoryIds){
        //기존 카테고리 가중치 모두 삭제
        categoryWeightRepository.deleteByMemberId(memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("MemberPreferredCategoryServiceModificationService.changeMemberPreferredCategory() 존재하지 않는 사용자 입니다."));
        List<Category> categories =categoryRepository.findIds(updateCategoryIds);

        if (categories.isEmpty()) {
            throw new RuntimeException(
                    "MemberPreferredCategoryServiceModificationService.changeMemberPreferredCategory() - Cannot find such categories");
        }

        //새로운 카테고리 삽입
        List<CategoryWeight> updatedMemberCategoryWeights = categories.stream()
                .map(category -> CategoryWeight.builder()
                        .member(member)
                        .preferred(true)
                        .category(category)
                        .value(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                        .build())
                .toList();

        categoryWeightRepository.saveAll(updatedMemberCategoryWeights);
    }

}
