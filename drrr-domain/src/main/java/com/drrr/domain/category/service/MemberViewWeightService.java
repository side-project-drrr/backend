package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberViewWeightService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final TechBlogPostRepository techBlogPostRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 사용자가 특정 게시물을 읽었을 때 그 게시물에 대한 가중치 증가
     */
    public void increaseMemberViewPost(final Long memberId, final Long postId, final List<Long> categoryIds) {
        TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> new RuntimeException(
                        "MemberViewWeightService.increaseMemberViewPost() - Cannot find such post -> postId : "
                                + postId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException(
                "MemberViewWeightService.increaseMemberViewPost() - Cannot find such member -> memberId : "
                        + memberId));

        //조회수 증가
        post.increaseViewCount();

        List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        //사용자의 category weight에 대한 정보가 없는 경우 새로 삽입
        if (categoryWeights.isEmpty()) {
            List<Category> categories = categoryRepository.findIds(categoryIds);
            if (categories.isEmpty()) {
                throw new RuntimeException(
                        "MemberViewWeightService.increaseMemberViewPost() - Cannot find such categories");
            }
            List<CategoryWeight> updatedCategoryWeights = categories.stream()
                    .map(category -> CategoryWeight.builder()
                            .preferred(false)
                            .member(member)
                            .category(category)
                            .value(WeightConstants.INCREASE_WEIGHT.getValue())
                            .build())
                    .toList();
            categoryWeightRepository.saveAll(updatedCategoryWeights);
        } else {
            //기존에 해당 게시물을 읽은 기록이 있다면 가중치 증가
            categoryWeights.forEach(CategoryWeight::accumulateWeight);
        }

    }

}
