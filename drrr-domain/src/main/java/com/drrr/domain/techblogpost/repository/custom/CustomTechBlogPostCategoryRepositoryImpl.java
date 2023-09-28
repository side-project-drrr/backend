package com.drrr.domain.techblogpost.repository.custom;

import com.drrr.core.recommandation.constant.constant.PostConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.service.RecommendPostService.ExtractedPostCategoryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class CustomTechBlogPostCategoryRepositoryImpl implements CustomTechBlogPostCategoryRepository{
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<ExtractedPostCategoryDto> getFilteredPost(List<CategoryWeightDto> categoryWeightDtos, Long memberId) {
        //query에 in(...) 절을 위한 categoryId 리스트
        List<Long> categoryIds = categoryWeightDtos.stream()
                .map((categoryWeightDto) -> {
                    return categoryWeightDto.category().getId();
                }).toList();

        int LIMIT_POST_FACTOR = PostConstants.RECOMMEND_POSTS_COUNT.getValue() * categoryIds.size();

        String unionSql = categoryIds.stream()
                .map(categoryId -> String.format("""
                             SELECT A.techblogpost_id pid
                                     , A.category_id cid
                                     , X.created_date created_date
                                  FROM DRRR_TECHBLOGPOST_CATEGORY A
                            INNER JOIN (
                                               SELECT C.id
                                                    , C.created_date
                                                 FROM DRRR_TECHBLOGPOST_CATEGORY B
                                           INNER JOIN DRRR_TECH_BLOG_POST C
                                                   ON B.techblogpost_id = C.id
                                LEFT JOIN DRRR_MEMBER_POST_LOG D
                                       ON B.techblogpost_id = D.post_id
                                      AND D.member_id = %d
                                    WHERE D.post_id IS NULL
                                      AND B.category_id = %d
                                    ORDER BY C.created_date DESC
                                    LIMIT %d
                            ) X
                        ON A.techblogpost_id = X.id
                            """, memberId, categoryId, LIMIT_POST_FACTOR))
                .collect(Collectors.joining(" UNION ALL "));

        String refactorSql = String.format("""
                SELECT T.pid,T.cid, T.created_date FROM (
                 %s
                ) T GROUP BY T.pid, T.cid, T.created_date
                 ORDER BY T.created_date DESC
                """, unionSql);

        Query nativeQuery = em.createNativeQuery(refactorSql);

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
}
