package com.drrr.infra.push.repository.impl;

import static com.drrr.infra.push.entity.QPushStatus.pushStatus1;
import static com.drrr.domain.category.entity.QCategoryWeight.categoryWeight;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.domain.category.dto.MemberPostsDto;
import com.drrr.domain.category.dto.PushPostDto;
import com.drrr.infra.push.dto.PushDateDto;
import com.drrr.infra.push.repository.CustomPushStatusRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomPushStatusRepositoryImpl implements CustomPushStatusRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PushPostDto> findPushByMemberIds(final Long memberIds) {

        //정해진 페이지에 해당하는 member ids에 해당하는 member : posts 가져오기
        List<MemberPostsDto> memberPostsDtos = queryFactory
                .select(
                        Projections.constructor(MemberPostsDto.class
                                , categoryWeight.member.id
                                , techBlogPost.id)
                )
                .from(categoryWeight)
                .leftJoin(techBlogPostCategory)
                .on(categoryWeight.category.id.eq(techBlogPostCategory.category.id),
                        categoryWeight.member.id.eq(memberIds))
                .leftJoin(techBlogPost)
                .on(techBlogPostCategory.post.id.eq(techBlogPost.id))
                .where(techBlogPost.writtenAt.eq(LocalDate.now()))
                .fetch();

        //Key : memberId, Value : postIds
        //memberId에게 푸시해줘야 하는 post ids
        Map<Long, List<Long>> memberPostsMap = memberPostsDtos.stream()
                .collect(Collectors.groupingBy(
                        MemberPostsDto::memberId,
                        Collectors.mapping(MemberPostsDto::postId, Collectors.toList())
                ));

        return memberPostsMap.keySet()
                .stream()
                .map(key -> {
                    return PushPostDto.builder()
                            .memberId(key)
                            .postIds(memberPostsMap.get(key))
                            .build();
                }).toList();
    }

    @Override
    public List<PushDateDto> findPushDateCountAndStatusByMemberIdAndPushDate(final Long memberId, final int count) {
        return queryFactory.select(
                        Projections.constructor(PushDateDto.class
                                , new CaseBuilder()
                                                .when(pushStatus1.openStatus.eq(Boolean.TRUE))
                                                .then(1)
                                                .otherwise(0)
                                                .max().eq(1).as("openStatus")
                                , new CaseBuilder()
                                                .when(pushStatus1.readStatus.eq(Boolean.TRUE))
                                                .then(1)
                                                .otherwise(0)
                                                .max().eq(1).as("readStatus")
                                , pushStatus1.id.count()
                                , pushStatus1.pushDate
                        )
                )
                .from(pushStatus1)
                .innerJoin(pushStatus1.postIds)
                .where(pushStatus1.memberId.eq(memberId))
                .groupBy(pushStatus1.id)
                .limit(count)
                .orderBy(pushStatus1.pushDate.desc())
                .fetch();
    }

}
