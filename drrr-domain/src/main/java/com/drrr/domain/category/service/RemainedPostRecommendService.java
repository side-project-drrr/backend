package com.drrr.domain.category.service;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RemainedPostRecommendService {
    private JPAQueryFactory queryFactory;
    @PersistenceContext
    private EntityManager em;

    public List<Long> recommendRemain(Long memberId, int remainedPostCount) {
        queryFactory = new JPAQueryFactory(em);

        return queryFactory.select(techBlogPost.id)
                .from(techBlogPost)
                .leftJoin(memberPostLog)
                .on(
                        memberPostLog.postId.eq(techBlogPost.id)
                        , memberPostLog.isRead.eq(false)
                        , memberPostLog.isRecommended.eq(false))
                .where(memberPostLog.memberId.eq(memberId))
                .orderBy(techBlogPost.createdDate.desc())
                .limit(remainedPostCount)
                .fetch();
    }
}
