package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.MemberPreferredCategoryServiceModificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalMemberPreferredCategoryModificationService {
    private final MemberPreferredCategoryServiceModificationService modificationService;


    public void execute(final Long memberId, final List<Long> categoryIds) {
        modificationService.changeMemberPreferredCategory(memberId, categoryIds);
    }
}
