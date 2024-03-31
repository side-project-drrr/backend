package com.drrr.domain.category.service;

import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// external -> 이거 명칭이 애매함
//
@RequiredArgsConstructor
@Service
@Slf4j
public class RecommendPostService {
    private final MemberPostLogRepository memberPostLogRepository;
    private final RecommendPostExtractService recommendPostExtractService;

    @Transactional(readOnly = true) // readonly ..?
    public List<Long> recommendPosts(final Long memberId, int count) {

        //오늘 추천은 받았으나 안 읽었던 추천 게시물 다시 가져와서 반환
        List<Long> todayUnreadRecommendPostIds = memberPostLogRepository.findTodayUnreadRecommendPostIds(memberId);

        int requirePostCount = count;

        //오늘 추천은 받았으나 안 읽었던 추천 게시물 다시 가져와서 반환
        if (requirePostCount == todayUnreadRecommendPostIds.size()){
            return todayUnreadRecommendPostIds;
        }

        //추천해줘야 하는 게시물 수가 오늘 추천은 받았으나 안 읽었던 추천 게시물 수보다 작을 때 안 읽었던 추천 게시물에서 그대로 반환
        if (requirePostCount < todayUnreadRecommendPostIds.size()){
            return todayUnreadRecommendPostIds.subList(0, requirePostCount);
        }

        //새로운 서비스 클래스로 분리
        //오늘 추천은 받았으나 안 읽었던 추천 게시물은 유지하고 추가적으로 추천해줘야 하는 게시물 수를 계산
        requirePostCount -= todayUnreadRecommendPostIds.size();

        //카테고리_가중치 Mapping Table를 특정 MemberId로 조회

        return recommendPostExtractService.extractRecommendPostIds(requirePostCount, memberId, todayUnreadRecommendPostIds);
    }

    @Builder
    public record ExtractedPostCategoryDto(
            Long postId,
            Long categoryId
    ) {
        @QueryProjection
        public ExtractedPostCategoryDto(Long postId, Long categoryId) {
            this.postId = postId;
            this.categoryId = categoryId;
        }
    }
}