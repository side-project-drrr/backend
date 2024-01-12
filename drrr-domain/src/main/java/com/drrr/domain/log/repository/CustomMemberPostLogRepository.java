package com.drrr.domain.log.repository;

import com.drrr.domain.log.entity.post.MemberPostLog;

import java.util.List;

public interface CustomMemberPostLogRepository {
    List<MemberPostLog> updateMemberPostLog(final Long memberId, final List<Long> postIds);

}
