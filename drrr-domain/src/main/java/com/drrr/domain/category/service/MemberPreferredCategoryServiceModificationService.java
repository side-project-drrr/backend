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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    private final WeightValidationService weightValidationService;

    public void changeMemberPreferredCategory(final Long memberId, final List<Long> updateCategoryIds) {
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            log.error("사용자를 찾을 수 없습니다.");
            log.error("memberId -> " + memberId);
            return MemberExceptionCode.MEMBER_NOT_FOUND.newInstance();
        });

        List<Category> categories = categoryRepository.findByIdIn(updateCategoryIds);
        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다.");
            log.error("category id -> " + updateCategoryIds.toString());
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

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
                    //선호 카테고리가 아니였던 카테고리가 선호 카테고리로 바꿔야 한다면 true
                    boolean isPreferred = updateCategoryIdsSet.contains(categoryWeight.getCategory().getId());
                    return CategoryWeight.builder()
                            .member(categoryWeight.getMember())
                            .preferred(isPreferred)
                            .weightValue(categoryWeight.getWeightValue())
                            .lastReadAt(LocalDateTime.now())
                            .category(categoryWeight.getCategory())
                            .build();
                })
                .toList();

        //사용자의 기존 등록된 카테고리 id를 제외하고 새로 등록해줘야 할 id set
        updateCategoryIdsSet.removeAll(existingCategoryIdsSet);

        //전체 카테고리 정보 가져오기
        //따로 exception 처리가 없는 이유는 결과 값이 0이면 바꾸려는 선호 카테고리가 이미 기존 선호 카테고리나 비선호 카테고리로 다 등록되어 있는 경우임
        final List<Category> remainedCategories = categoryRepository.findByIdIn(updateCategoryIdsSet);

        //카테고리 가중치 테이블에 없는 신규 카테고리 가중치 추가
        final List<CategoryWeight> updates = Stream.concat(remainedCategories.stream()
                .map(category -> CategoryWeight.builder()
                        .member(member)
                        .preferred(true)
                        .weightValue(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                        .lastReadAt(LocalDateTime.now())
                        .category(category)
                        .build()), updatedCategoryWeights.stream()).toList();

        //memberId에 해당하는 categoryWeight를 다 지우기
        categoryWeightRepository.deleteByMemberId(memberId);

        //다시 새로 categoryWeight를 넣어주기
        categoryWeightRepository.saveAll(updates);

        //가중치 검증, 선호 카테고리가 최소 가중치에 못 미치는 경우가 있을 수 있음
        weightValidationService.validateWeight(memberId);
    }

}
