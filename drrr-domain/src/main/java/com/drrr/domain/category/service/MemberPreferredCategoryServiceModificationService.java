package com.drrr.domain.category.service;

import com.drrr.core.exception.category.CategoryExceptionCode;
import com.drrr.core.exception.member.MemberExceptionCode;
import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MemberPreferredCategoryServiceModificationService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public void changeMemberPreferredCategory(final Long memberId, final List<Long> updateCategoryIds) {
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            log.error("사용자 가중치를 찾을 수 없습니다.");
            log.error("memberId -> " + memberId);
            throw MemberExceptionCode.MEMBER_NOT_FOUND.newInstance();
        });

        //현재 사용자의 카테고리 정보 가져오기
        final List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);
        if (categoryWeights.isEmpty()) {
            log.error("카테고리 가중치 정보를 찾을 수 없습니다.");
            log.error("memberId -> " + memberId);
            throw CategoryExceptionCode.CATEGORY_WEIGHT_NOT_FOUND.newInstance();
        }

        //사용자의 기존 선호하는 카테고리 id set
        final Set<Long> existingCategoryIdsSet = categoryWeights.stream()
                .map(categoryWeight -> categoryWeight.getCategory().getId()).collect(Collectors.toSet());

        //사용자가 변경한 선호하는 카테고리 id set
        final Set<Long> updateCategoryIdsSet = new HashSet<>(updateCategoryIds);

        //기존에 있던 사용자의 카테고리 가중치 정보를 업데이트
        final List<CategoryWeight> updatedCategoryWeights = categoryWeights.stream()
                .map(categoryWeight -> {
                    boolean isPreferred = updateCategoryIdsSet.contains(categoryWeight.getCategory().getId());
                    return CategoryWeight.builder()
                            .member(categoryWeight.getMember())
                            .preferred(isPreferred)
                            .value(categoryWeight.getValue())
                            .category(categoryWeight.getCategory())
                            .build();
                }).collect(Collectors.toList());

        //사용자의 기존 등록된 카테고리 id를 제외하고 새로 등록해줘야 할 id set
        updateCategoryIdsSet.removeAll(existingCategoryIdsSet);

        //전체 카테고리 정보 가져오기
        //따로 exception 처리가 없는 이유는 결과 값이 0이면 바꾸려는 선호 카테고리가 이미 기존 선호 카테고리나 비선호 카테고리로 다 등록되어 있는 경우임
        final List<Category> remainedCategories = categoryRepository.findByIdIn(updateCategoryIdsSet);

        //카테고리 가중치 테이블에 없는 신규 카테고리 가중치 추가
        final List<CategoryWeight> remainedCategoryWeights = remainedCategories.stream()
                .map(category -> {
                    return CategoryWeight.builder()
                            .member(member)
                            .preferred(true)
                            .value(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                            .category(category)
                            .build();
                }).collect(Collectors.toList());

        //memberId에 해당하는 categoryWeight를 다 지우기
        categoryWeightRepository.deleteByMemberId(memberId);

        //바꾸려는 선호 카테고리가 이미 기존 선호 카테고리나 비선호 카테고리로 다 등록되지 않은 경우
        if (remainedCategoryWeights.size() != 0) {
            updatedCategoryWeights.addAll(remainedCategoryWeights);
        }

        //다시 새로 categoryWeight를 넣어주기
        categoryWeightRepository.saveAll(updatedCategoryWeights);
    }

}
