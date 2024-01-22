package com.drrr.domain.category.service;

import com.drrr.domain.category.exception.CategoryExceptionCode;
import com.drrr.domain.member.exception.MemberExceptionCode;
import com.drrr.domain.techblogpost.exception.TechBlogExceptionCode;
import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.service.MemberPostLogService;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class MemberViewWeightService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final TechBlogPostRepository techBlogPostRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final MemberPostLogService memberPostLogService;

    /**
     * 사용자가 특정 게시물을 읽었을 때 그 게시물에 대한 가중치 증가
     */
    public void increaseMemberViewPost(final Long memberId, final Long postId, final List<Long> categoryIds) {
        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다.");
                    log.error("postId -> {}", +postId);
                    return TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다.");
                    log.error("memberId -> {}", memberId);
                    return MemberExceptionCode.MEMBER_NOT_FOUND.newInstance();
                });

        //조회수 증가
        post.increaseViewCount();

        final List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        //사용자의 category weight에 대한 정보가 없는 경우 새로 삽입
        if (categoryWeights.isEmpty()) {
            final List<Category> categories = categoryRepository.findIds(categoryIds);
            if (categories.isEmpty()) {
                log.error("카테고리를 찾을 수 없습니다.");
                log.error("categoryIds -> {}", categoryIds);
                throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
            }

            final List<CategoryWeight> updatedCategoryWeights = categories.stream()
                    .map(category -> CategoryWeight.builder()
                            .preferred(false)
                            .member(member)
                            .category(category)
                            .weightValue(WeightConstants.INCREASE_WEIGHT.getValue())
                            .build())
                    .toList();
            categoryWeightRepository.saveAll(updatedCategoryWeights);
            return;
        }
        //기존에 해당 게시물을 읽은 기록이 있다면 INCREASE_WEIGHT 값만큼 가중치 증가
        //단, 다시 읽는 게시물일 경우 새벽 12:00 를 넘어야지만 증가가 됨
        if (memberPostLogService.hasDayPassedAfterReading(memberId, postId)) {
            categoryWeights.forEach(CategoryWeight::accumulateWeight);
        }
    }


}
