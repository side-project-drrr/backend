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

    public List<Long> recommendRemain(Long memberId, int remainedPostCount) {
        return queryFactory.select(techBlogPost.id)
                .from(techBlogPost)
                .leftJoin(memberPostLog)
                .on(
                        memberPostLog.postId.eq(techBlogPost.id),
                        memberPostLog.memberId.eq(memberId)
                   )
                .where(memberPostLog.id.isNull())
                .orderBy(techBlogPost.createdDate.desc())
                .limit(remainedPostCount)
                .fetch();
    }
}
