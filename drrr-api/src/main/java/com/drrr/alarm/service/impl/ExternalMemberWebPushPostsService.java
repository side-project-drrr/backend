package com.drrr.alarm.service.impl;

import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostOuterDto;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ExternalMemberWebPushPostsService {
    private final CategoryWeightRepository categoryWeightRepository;
    private final TechBlogPostCategoryRepository techBlogPostCategoryRepository;

    public List<TechBlogPostOuterDto> execute(final Long memberId) {
        List<Long> categories = categoryWeightRepository.findCategoryIdsByMemberId(memberId);
        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다 -> {}", categories);
            throw DomainExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        return techBlogPostCategoryRepository.getUniquePostsByCategoryIds(categories);

    }
}
