package com.drrr.domain.techblogpost.repository.custom;

import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.core.recommandation.constant.constant.PostConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.service.RecommendPostService.ExtractedPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Repository
public class CustomTechBlogPostCategoryRepositoryImpl implements CustomTechBlogPostCategoryRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    @Override
    public List<ExtractedPostCategoryDto> getFilteredPost(final List<CategoryWeightDto> categoryWeightDtos,
                                                          final Long memberId) {
        //query에 in(...) 절을 위한 categoryId 리스트
        final List<Long> categoryIds = categoryWeightDtos.stream()
                .map((categoryWeightDto) -> {
                    return categoryWeightDto.category().getId();
                }).toList();

        final int LIMIT_POST_FACTOR = PostConstants.RECOMMEND_POSTS_COUNT.getValue() * categoryIds.size();

        //pid 기술블로그 id, cid 카테고리 id, created_date 작성일자
        //로그에는 존재하지 않는 기술 블로그를 추천해줌(읽은 적도 없고, 추천 받은 적이 없는 기술블로그)
        final String unionSql = categoryIds.stream()
                .map(categoryId -> String.format("""
                             SELECT A.techblogpost_id pid
                                     , A.category_id cid
                                     , X.written_at written_at
                                  FROM DRRR_TECHBLOGPOST_CATEGORY A
                            INNER JOIN (
                                               SELECT C.id
                                                    , C.written_at
                                                 FROM DRRR_TECHBLOGPOST_CATEGORY B
                                           INNER JOIN DRRR_TECHBLOGPOST C
                                                   ON B.techblogpost_id = C.id
                                LEFT JOIN DRRR_MEMBER_POST_LOG D
                                       ON B.techblogpost_id = D.post_id
                                      AND D.member_id = %d
                                    WHERE D.post_id IS NULL
                                      AND B.category_id = %d
                                    ORDER BY C.written_at DESC
                                    LIMIT %d
                            ) X
                        ON A.techblogpost_id = X.id
                            """, memberId, categoryId, LIMIT_POST_FACTOR))
                .collect(Collectors.joining(" UNION ALL "));

        final String refactorSql = String.format("""
                SELECT T.pid,T.cid, T.written_at FROM (
                 %s
                ) T GROUP BY T.pid, T.cid, T.written_at
                 ORDER BY T.written_at DESC
                """, unionSql);

        Query nativeQuery = em.createNativeQuery(refactorSql);

        @SuppressWarnings("unchecked") final List<Object[]> list = nativeQuery.getResultList();

        //가장 최근에 만들어진 게시물 순으로 정렬됨
        //사용자가 관심 있는 카테고리에 대해 게시물 추출
        return list.stream()
                .map(elem -> ExtractedPostCategoryDto.builder()
                        .postId((Long) elem[0])
                        .categoryId((Long) elem[1])
                        .build())
                .toList();
    }

    @Override
    public List<TechBlogPostBasicInfoDto> getUniquePostsByCategoryIds(final List<Long> categoryIds) {
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')"
                , techBlogPost.createdAt);
        return queryFactory.select(Projections.constructor(TechBlogPostBasicInfoDto.class
                        , techBlogPost.id
                        , techBlogPost.title
                        , techBlogPost.summary
                        , techBlogPost.techBlogCode
                        , techBlogPost.thumbnailUrl
                        , techBlogPost.viewCount
                        , techBlogPost.postLike
                        , techBlogPost.writtenAt))
                .from(techBlogPost)
                .where(techBlogPost.id.in(
                        queryFactory
                                .select(techBlogPostCategory.post.id)
                                .from(techBlogPostCategory)
                                .where(techBlogPostCategory.category.id.in(categoryIds),
                                        formattedDate.eq(String.valueOf(LocalDate.now())))
                                .groupBy(techBlogPostCategory.post.id))).fetch();
    }
}
