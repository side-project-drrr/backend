package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.entity.history.MemberPostHistory;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostHistoryRepository;
import com.drrr.domain.log.repository.MemberTechBlogPostRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberViewWeightService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final MemberPostHistoryRepository memberPostHistoryRepository;

    private final MemberTechBlogPostRepository memberTechBlogPostRepository;
    private final TechBlogPostRepository techBlogPostRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 사용자가 특정 게시물을 읽었을 때 그 게시물에 대한 가중치 증가
     */
    public void increaseMemberViewPost(Long memberId, Long postId, List<Long> categoryIds) {
        TechBlogPost post = techBlogPostRepository.findById(postId).orElseThrow(() -> new RuntimeException(
                "MemberViewWeightService.increaseMemberViewPost - Cannot find such post -> postId : " + postId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException(
                "MemberViewWeightService.increaseMemberViewPost - Cannot find such member -> memberId : " + memberId));
        //조회수 증가
        post.increaseViewCount();

        List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        //사용자의 category weight에 대한 정보가 없는 경우 새로 삽입
        if (categoryWeights.isEmpty()) {
            List<Category> categories = categoryRepository.findByIdIn(categoryIds);
            if(categories.isEmpty()){
                throw new RuntimeException("MemberViewWeightService.increaseMemberViewPost - Cannot find such categories");
            }
            categoryWeightRepository.saveAll(categories.stream()
                    .map(category -> CategoryWeight.builder()
                            .preferred(false)
                            .member(member)
                            .category(category)
                            .value(WeightConstants.INCREASE_WEIGHT.getValue())
                            .build())
                    .toList());
        } else {
            //기존에 해당 게시물을 읽은 기록이 있다면 가중치 증가
            categoryWeights.forEach(CategoryWeight::accumulateWeight);
        }

        //사용자 게시물 히스토리 저장
        insertMemberPostHistory(postId, memberId);
        //사용자 게시물 로그 저장
        insertMemberPostLog(memberId, postId);

    }

    private void insertMemberPostLog(Long memberId, Long postId) {
        memberTechBlogPostRepository.findByPostId(postId)
                .orElseGet(() ->  memberTechBlogPostRepository.save(MemberPostLog.builder()
                                .memberId(memberId)
                                .postId(postId)
                                .isRead(true)
                                .isRecommended(false)
                                .build()));
    }

    private void insertMemberPostHistory(Long postId, Long memberId) {
        MemberPostHistory history = MemberPostHistory.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
        memberPostHistoryRepository.save(history);
    }
}
