package com.drrr.domain.log.repository;

import java.util.List;

public interface CustomMemberPostLogRepository {
    List<Long> findTodayUnreadRecommendPostIds(final Long memberId);

}
