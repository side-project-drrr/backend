package com.drrr.domain.category.repository.impl;

import static com.drrr.domain.category.entity.QCategory.category;
import static com.drrr.domain.category.entity.QCategoryWeight.categoryWeight;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryPostDto;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CustomCategoryRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<Category> findIds(List<Long> ids) {
        return queryFactory.select(category)
                .from(category)
                .where(category.id.in(ids))
                .fetch();
    }

    @Override
    public List<CategoryDto> findCategoriesByMemberId(Long memberId) {
        return queryFactory.select(Projections.constructor(CategoryDto.class, category.id, category.name))
                .from(category)
                .innerJoin(categoryWeight)
                .on(category.id.eq(categoryWeight.category.id), categoryWeight.member.id.eq(memberId),
                        categoryWeight.preferred.eq(true))
                .orderBy(category.name.asc())
                .fetch();
    }

    @Override
    public List<CategoryDto> findCategoriesByPostId(final Long postId) {
        return queryFactory.select(Projections.constructor(CategoryDto.class, category.id, category.name))
                .from(category)
                .innerJoin(techBlogPostCategory)
                .on(category.id.eq(techBlogPostCategory.category.id), techBlogPostCategory.post.id.eq(postId))
                .orderBy(category.name.asc())
                .fetch();
    }

    private BooleanExpression languageCondition(final LanguageConstants language,
                                                IndexConstants indexConstants) {
        if (language.equals(LanguageConstants.ENGLISH)) {
            return category.name.toUpperCase().like(
                    indexConstants.getCharacter() + "%");
        }
        return category.name.goe(String.valueOf(indexConstants.getCharacter())).
                and(category.name.lt(String.valueOf(indexConstants.getNext().getCharacter())));
    }

    @Override
    public Slice<CategoryDto> findCategoryByNameLike(final LanguageConstants language,
                                                     IndexConstants indexConstants,
                                                     Pageable pageable) {

        List<CategoryDto> content = queryFactory.select(
                        Projections.constructor(CategoryDto.class
                                , category.id
                                , category.name))
                .from(category)
                .where(languageCondition(language, indexConstants))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(category.count())
                .from(category)
                .where(languageCondition(language, indexConstants))
                .fetchOne();

        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<CategoryPostDto> findEachPostCategoriesByPostIds(final List<Long> postId,
                                                                 @Nullable OrderSpecifier<Integer> orderCondition) {
        return queryFactory.select(Projections.constructor(CategoryPostDto.class
                        , category.id
                        , techBlogPostCategory.post.id
                        , category.name))
                .from(category)
                .innerJoin(techBlogPostCategory)
                .on(category.id.eq(techBlogPostCategory.category.id))
                .where(techBlogPostCategory.post.id.in(postId))
                .orderBy(techBlogPostCategory.post.writtenAt.desc(), orderCondition)
                .fetch();
    }

    private String koreanRangedCategoriesQueryFactory(IndexConstants[] rangeIndexConstants, IndexConstants startIdx,
                                                      IndexConstants endIdx, int size) {
        return IntStream.rangeClosed(startIdx.ordinal(), endIdx.ordinal()).mapToObj(i -> {
            Character startChar = Character.toUpperCase(rangeIndexConstants[i].getCharacter());
            Character nextChar = Character.toUpperCase(rangeIndexConstants[i].getNext().getCharacter());
            return String.format("""
                            (
                               SELECT A.id id
                                    , A.name name
                                    , '%s' keyIndex
                                 FROM DRRR_CATEGORY A 
                                WHERE A.name >= '%s' AND A.name < '%s'
                                LIMIT %d
                             )
                            """, startChar
                    , startChar
                    , nextChar
                    , size);
        }).collect(Collectors.joining(" UNION ALL "));
    }

    private String englishRangedCategoriesQueryFactory(IndexConstants[] rangeIndexConstants,
                                                       IndexConstants startIdx, IndexConstants endIdx, int size) {
        return IntStream.rangeClosed(startIdx.ordinal(), endIdx.ordinal()).mapToObj(i -> {
            Character startChar = Character.toUpperCase(rangeIndexConstants[i].getCharacter());
            Character endChar = Character.toUpperCase(rangeIndexConstants[i].getCharacter());
            return String.format("""
                            (
                               SELECT A.id id
                                    , A.name name
                                    , '%s' keyIndex
                                 FROM DRRR_CATEGORY A
                                WHERE A.name LIKE '%s%%'
                                LIMIT %d
                             )
                            """, startChar
                    , endChar
                    , size);
        }).collect(Collectors.joining(" UNION ALL "));
    }


    @Override
    public List<CategoriesKeyDto> findRangedCategories(IndexConstants startIdx, IndexConstants endIdx,
                                                       LanguageConstants language, int size) {

        final IndexConstants[] rangeIndexConstants = IndexConstants.values();

        final String unionSql = language.equals(LanguageConstants.ENGLISH) ?
                englishRangedCategoriesQueryFactory(rangeIndexConstants, startIdx, endIdx, size) :
                koreanRangedCategoriesQueryFactory(rangeIndexConstants, startIdx, endIdx, size);

        @SuppressWarnings("unchecked") final List<Object[]> list = generateNativeQueryResultList(unionSql);

        //가장 최근에 만들어진 게시물 순으로 정렬됨
        //사용자가 관심 있는 카테고리에 대해 게시물 추출
        return list.stream()
                .map(elem -> CategoriesKeyDto.builder()
                        .id((Long) elem[0])
                        .name((String) elem[1])
                        .keyIndex(((Character) elem[2]).toString())
                        .build())
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> generateNativeQueryResultList(String unionSql) {
        Query nativeQuery = em.createNativeQuery(unionSql);
        return nativeQuery.getResultList();
    }

    @Override
    public List<CategoriesKeyDto> findRangedEtcCategories(int size) {
        String query = """
                (
                   SELECT A.id id
                        , A.name name
                        , '기타' keyIndex
                     FROM DRRR_CATEGORY A
                    WHERE A.name NOT REGEXP '^[A-Za-z가-힣]'
                 )
                """;

        @SuppressWarnings("unchecked") final List<Object[]> list = generateNativeQueryResultList(query);

        //가장 최근에 만들어진 게시물 순으로 정렬됨
        //사용자가 관심 있는 카테고리에 대해 게시물 추출
        return list.stream()
                .map(elem -> CategoriesKeyDto.builder()
                        .id((Long) elem[0])
                        .name((String) elem[1])
                        .keyIndex(((String) elem[2]))
                        .build())
                .toList();

    }

    @Override
    public Slice<CategoriesKeyDto> findEtcCategoriesPage(Pageable pageable) {

        String query = String.format("""
                (
                   SELECT A.id id
                        , A.name name
                        , '기타' keyIndex
                     FROM DRRR_CATEGORY A
                    WHERE A.name NOT REGEXP '^[A-Za-z가-힣]'
                    LIMIT %d
                    OFFSET %d 
                 )
                """, pageable.getPageSize(), pageable.getOffset());

        @SuppressWarnings("unchecked") final List<Object[]> list = generateNativeQueryResultList(query);

        //가장 최근에 만들어진 게시물 순으로 정렬됨
        //사용자가 관심 있는 카테고리에 대해 게시물 추출
        List<CategoriesKeyDto> categoriesKeyDtos = list.stream()
                .map(elem -> CategoriesKeyDto.builder()
                        .id((Long) elem[0])
                        .name((String) elem[1])
                        .keyIndex(((String) elem[2]))
                        .build())
                .toList();

        String count = """
                   SELECT count(*)
                     FROM DRRR_CATEGORY A
                    WHERE A.name NOT REGEXP '^[A-Za-z가-힣]'
                """;

        Query nativeQuery = em.createNativeQuery(count);
        Object singleResult = nativeQuery.getSingleResult();

        Long total = (Long) singleResult;

        boolean hasNext = (pageable.getOffset() + categoriesKeyDtos.size()) < total;

        return new SliceImpl<>(categoriesKeyDtos, pageable, hasNext);
    }

    @Override
    public List<Category> findByNameContaining(final String text, final Pageable pageable) {
        final BooleanExpression uniqueNameOrDisplayNameSearchCondition = category.name.startsWith(text);

        return queryFactory.select(category)
                .from(category)
                .where(uniqueNameOrDisplayNameSearchCondition)
                .limit(pageable.getPageSize())
                .orderBy(category.name.asc())
                .fetch();
    }

    @Override
    public List<Category> findTopCategories(Long count) {
        return queryFactory.select(categoryWeight.category)
                .from(categoryWeight)
                .where(categoryWeight.preferred.eq(true))
                .groupBy(categoryWeight.category.id)
                .orderBy(categoryWeight.category.id.count().desc())
                .limit(count)
                .fetch();
    }

    @Builder
    public record CategoriesKeyDto(
            Long id,
            String name,
            String keyIndex) {
    }
}
