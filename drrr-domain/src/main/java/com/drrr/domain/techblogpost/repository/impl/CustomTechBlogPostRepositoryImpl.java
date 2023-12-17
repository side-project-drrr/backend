package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;

import com.drrr.domain.techblogpost.repository.CustomTechBlogPostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomTechBlogPostRepositoryImpl implements CustomTechBlogPostRepository {
    private final JPAQueryFactory queryFactory;

    public List<Long> recommendRemain(final Long memberId, final int remainedPostCount) {
        //로그 테이블에서 사용자가 읽은 적이 없는 최신 기술블로그를 추천
        return queryFactory.select(techBlogPost.id)
                .from(techBlogPost)
                .leftJoin(memberPostLog)
                .on(
                        memberPostLog.postId.eq(techBlogPost.id),
                        memberPostLog.memberId.eq(memberId)
                )
                .where(memberPostLog.id.isNull())
                .orderBy(techBlogPost.writtenAt.desc())
                .limit(remainedPostCount)
                .fetch();
    }
}
