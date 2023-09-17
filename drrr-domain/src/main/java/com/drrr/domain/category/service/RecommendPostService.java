package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.PostConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.service.MemberPostReadLogService;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecommendPostService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final MemberPostReadLogService memberPostReadLogService;
    private final RemainedPostRecommendService remainedPostRecommendService;

    private final TechBlogPostRepository techBlogPostRepository;

    private int remainingPostCount = 0;

    @PersistenceContext
    private EntityManager em;

    public List<TechBlogPost> recommendPosts(Long memberId) {
        //카테고리_가중치 Mapping Table를 특정 MemberId로 조회
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId).orElseThrow(
                () -> new RuntimeException(
                        "RecommendPostService.recommendPosts - Cannot find such element -> memberId : " + memberId));

        //entity -> dto 변환
        List<CategoryWeightDto> categoryWeightDtos = categoryWeights.stream()
                .map(categoryWeight -> {
                    return CategoryWeightDto.builder()
                            .member(categoryWeight.getMember())
                            .category(categoryWeight.getCategory())
                            .value(categoryWeight.getValue())
                            .preferred(categoryWeight.isPreferred())
                            .build();
                }).toList();

        //사용자에게 추천해줄 수 있는 모든 게시물 가져오기
        List<ExtractedPostCategoryDto> techBlogPosts = getFilteredPost(categoryWeightDtos, memberId);

        //게시물에 대해 카테고리별로 정리
        Map<Long, Map<Long, String>> classifiedPostsDto = classifyPostWithCategories(techBlogPosts);

        //추천할 게시물 ids를 카테고리별로 담아서 반환
        //postsPerCategoryMap -> key : categoryId, value : 할당해야 하는 게시물 개수
        Map<Long, Integer> postsPerCategoryMap = calculatePostDistribution(categoryWeightDtos);

        //카테고리별로 할당된 개수만큼 게시물 추천해서 id 값 담아놓은 리스트
        //postIds - 할당된 게시물 id 리스트
        List<Long> postIds = extractRecommendPostIds(classifiedPostsDto, postsPerCategoryMap);

        //추천해줄 게시물이 더이상 없는 경우
        if (remainingPostCount > 0) {
            postIds.addAll(remainedPostRecommendService.recommendRemain(memberId, remainingPostCount));
        }

        //추천해주는 게시물에 대해서 카테고리_가중치 테이블의 isRecommended 업데이트
        memberPostReadLogService.updateMemberPostLog(memberId, postIds, false, true);

        //post ids에 해당하는 post 객체들 찾기
        List<TechBlogPost> posts = techBlogPostRepository.findAllById(postIds);

        return posts;
    }

    /**
     * 카테고리 별로 몇개의 게시물을 추천해줄 건지 계산 return : key - 카테고리_아이디, value - 카테고리별 추천 게시물 수 categoryWeightDtos - 가장 최근 게시물 순으로
     * 정렬되어 있는 상태
     */
    private Map<Long, Integer> calculatePostDistribution(List<CategoryWeightDto> categoryWeightDtos) {
        int totalPosts = PostConstants.RECOMMEND_POSTS_COUNT.getValue();

        // Calculate the total weight
        double totalWeight = categoryWeightDtos.stream().mapToDouble(CategoryWeightDto::value).sum();

        // key : categoryId, value : 가중치 백분율
        Map<Long, Double> fractionalPartsMap = new LinkedHashMap<>();
        // 카테고리별 게시물 개수
        List<Integer> postCounts = new ArrayList<>();

        categoryWeightDtos.forEach(dto -> {
            double rawPostCount = (dto.value() / totalWeight) * totalPosts;
            int postCount = (int) rawPostCount;
            double fractionalPart = rawPostCount - postCount;

            postCounts.add(postCount);
            fractionalPartsMap.put(dto.category().getId(), fractionalPart);
        });

        Map<Long, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < postCounts.size(); i++) {
            resultMap.put(categoryWeightDtos.get(i).category().getId(), postCounts.get(i));
        }

        int remainingPosts = totalPosts - postCounts.stream().mapToInt(Integer::intValue).sum();

        List<Map.Entry<Long, Double>> list = new ArrayList<>(fractionalPartsMap.entrySet());
        //가중치 비율이 제일 높은 순
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        for (Map.Entry<Long, Double> entry : list) {
            if (remainingPosts <= 0) {
                break;
            }

            if (resultMap.containsKey(entry.getKey())) {
                resultMap.put(entry.getKey(), resultMap.get(entry.getKey()) + 1);
            }

            remainingPosts--;
        }

        return resultMap;
    }

    /**
     * 카테고리별로 추천해줄 게시물들을 추출 classifiedPostsDto - 가장 최근 게시물 순으로 정렬되어 있는 상태 //코드 수정해야 함!
     */
    private List<Long> extractRecommendPostIds(Map<Long, Map<Long, String>> classifiedPostsDto,
                                               Map<Long, Integer> postsPerCategoryMap) {
        List<Long> postIds = new ArrayList<>();
        int totalPostCount = PostConstants.RECOMMEND_POSTS_COUNT.getValue();

        for (Long key : classifiedPostsDto.keySet()) {
            Map<Long, String> catMap = classifiedPostsDto.get(key);

            for (Long hkey : postsPerCategoryMap.keySet()) {
                Integer count = postsPerCategoryMap.get(hkey);
                if (count != null && count > 0 && catMap.containsKey(hkey)) {
                    postsPerCategoryMap.put(hkey, postsPerCategoryMap.get(hkey) - 1);
                    postIds.add(key);
                    totalPostCount--;
                    break;
                }
            }

            if (totalPostCount == 0) {
                break;
            }
        }

        //추천해줄 게시물이 없는지 확인
        remainingPostCount = totalPostCount;

        return postIds;
    }

    /**
     * 하나의 게시물에 대한 여러개의 카테고리를 객체로 각각 저장 techBlogPosts
     */
    private Map<Long, Map<Long, String>> classifyPostWithCategories(List<ExtractedPostCategoryDto> techBlogPosts) {

        Map<Long, Map<Long, String>> classifiedPostsMap = techBlogPosts.stream()
                .collect(Collectors.toMap(
                        ExtractedPostCategoryDto::postId,
                        //순서를 그대로 유지하기 위함
                        post -> new HashMap<Long, String>() {{
                            put(post.categoryId(), "category");
                        }},
                        (oldValue, newValue) -> {
                            oldValue.putAll(newValue);
                            return oldValue;
                        },
                        LinkedHashMap::new
                ));
        return classifiedPostsMap;

    }

    /**
     * 사용자에게 추천할 게시물을 모두 가져옴
     */
    @Transactional
    List<ExtractedPostCategoryDto> getFilteredPost(List<CategoryWeightDto> categoryWeightDtos, Long memberId) {
        //query에 in(...) 절을 위한 categoryId 리스트
        List<Long> categoryIds = categoryWeightDtos.stream()
                .map((categoryWeightDto) -> {
                    return categoryWeightDto.category().getId();
                }).toList();

        int LIMIT_POST_FACTOR = PostConstants.RECOMMEND_POSTS_COUNT.getValue() * categoryIds.size();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT T.pid");
        sql.append("     , T.cid ");
        sql.append("     , T.created_date ");
        sql.append("FROM ");
        sql.append("  (");
        int idx = 0;
        for (Long categoryId : categoryIds) {
            sql.append("      SELECT A.techblogpost_id pid");
            sql.append("           , A.category_id cid");
            sql.append("           , X.created_date created_date");
            sql.append("        FROM DRRR_TECHBLOGPOST_CATEGORY A");
            sql.append("  INNER JOIN (");
            sql.append("                     SELECT C.id");
            sql.append("                          , C.created_date");
            sql.append("                       FROM DRRR_TECHBLOGPOST_CATEGORY B");
            sql.append("                 INNER JOIN DRRR_TECHBLOGPOST C");
            sql.append("                         ON B.techblogpost_id = C.id");
            sql.append("                  LEFT JOIN DRRR_MEMBER_POST_LOG D");
            sql.append("                         ON B.techblogpost_id = D.post_id");
            sql.append("                        AND D.member_id = ").append(memberId);
            sql.append("                      WHERE D.post_id IS NULL");
            sql.append("                        AND B.category_id = ").append(categoryId);
            sql.append("                      ORDER BY C.created_date DESC");
            sql.append("                      LIMIT ").append(LIMIT_POST_FACTOR);
            sql.append("              ) X ");
            sql.append("          ON A.techblogpost_id = X.id");

            if (idx < categoryIds.size() - 1) {
                sql.append(" UNION ALL ");
            }
            idx++;
        }
        sql.append("  ) ");
        sql.append("T GROUP BY T.pid");
        sql.append("      , T.cid");
        sql.append("      , T.created_date");
        sql.append("  ORDER BY T.created_date DESC");

        System.out.println("sql 값: " + sql);

        Query nativeQuery = em.createNativeQuery(sql.toString());

        List<Object[]> list = nativeQuery.getResultList();
        List<ExtractedPostCategoryDto> resultDto = list.stream()
                .map(elem -> ExtractedPostCategoryDto.builder()
                        .postId((Long) elem[0])
                        .categoryId((Long) elem[1])
                        .build())
                .toList();

        //가장 최근에 만들어진 게시물 순으로 정렬됨
        //사용자가 관심 있는 카테고리에 대해 게시물 추출
        return resultDto;
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

    @Builder
    public record ExtractedPostDto(
            Long postId,
            List<Long> categoryIds
    ) {

    }


}
