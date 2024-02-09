package com.drrr.infra.push.repository;

import com.drrr.domain.category.dto.PushPostDto;
import java.util.List;

public interface CustomPushStatusRepository {
    public List<PushPostDto> findPushByMemberIds(final Long memberIds);
}
