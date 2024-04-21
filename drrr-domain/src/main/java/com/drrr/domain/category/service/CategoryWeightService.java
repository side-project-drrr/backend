package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.category.repository.common.CategoryQueryService;
import com.drrr.domain.log.service.MemberPostLogService;
import com.drrr.domain.member.entity.Member;
import java.time.LocalDateTime;
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
@Transactional
@RequiredArgsConstructor
public class CategoryWeightService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final CategoryRepository categoryRepository;
    private final MemberPostLogService memberPostLogService;
    private final CategoryQueryService categoryQueryService;

    public void updateCategoryWeights(final List<Long> categoryIds, final Member member, final Long postId) {
        final List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberIdAndCategoryIdsWithPessimisticLock(
                member.getId(), categoryIds);

        //카테고리의 최근 읽은 시간 업데이트, toList() 트리거 역할
        categoryWeights.stream()
                .peek(CategoryWeight::updateLastReadAt).toList();

        //기존 CategoryWeight에 없는 category id 찾기
        Set<Long> memberCategoryIdSet = categoryWeights.stream()
                .map(cw -> cw.getCategory().getId()).collect(Collectors.toSet());

        Set<Long> postCategoryIdSet = new HashSet<>(categoryIds);

        postCategoryIdSet.removeAll(memberCategoryIdSet);

        //테이블에 추가해야 할 새로운 카테고리 id가 존재하는 경우
        if (postCategoryIdSet.size() > 0) {
            List<Long> newCategoryIds = postCategoryIdSet.stream().toList();

            final List<Category> categories = categoryQueryService.getCategoriesByIds(newCategoryIds);

            final List<CategoryWeight> updatedCategoryWeights = categories.stream()
                    .map(category -> CategoryWeight.builder()
                            .preferred(false)
                            .member(member)
                            .category(category)
                            .lastReadAt(LocalDateTime.now())
                            .weightValue(WeightConstants.INITIAL_WEIGHT.getValue())
                            .build())
                    .toList();
            categoryWeightRepository.saveAll(updatedCategoryWeights);
        }

        //기존에 해당 게시물을 읽은 기록이 있다면 INCREASE_WEIGHT 값만큼 가중치 증가
        //단, 다시 읽는 게시물일 경우 새벽 12:00 를 넘어야지만 증가가 됨
        if (memberPostLogService.hasDayPassedAfterReading(member.getId(), postId)) {
            categoryWeights.forEach(CategoryWeight::accumulateWeight);
        }
    }
}
