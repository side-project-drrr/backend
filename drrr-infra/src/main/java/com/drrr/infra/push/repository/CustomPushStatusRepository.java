package com.drrr.infra.push.repository;

import com.drrr.domain.category.dto.PushPostDto;
import com.drrr.infra.push.dto.PushDateDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface CustomPushStatusRepository {
    public List<PushPostDto> findPushByMemberIds(final Long memberIds);
    public List<PushDateDto> findPushDateCountAndStatusByMemberIdAndPushDate(final Long memberId, final int count);
}
