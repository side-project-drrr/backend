package com.drrr.domain.techblogpost.service;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.common.AdminExceptionCode;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterPostTagService {
    private final TemporalTechBlogPostRepository temporalTechBlogPostRepository;
    private final TemporalTechPostTagRepository temporalTechPostTagRepository;
    private final CategoryRepository categoryRepository;

    public void execute(final Long postId, final List<String> tagNames, String aiSummarizedText) {
        AdminExceptionCode.DID_NOT_EXISTS_TAG_NAMES.invokeByCondition(tagNames.isEmpty());

        final var temporalTechBlogPost = temporalTechBlogPostRepository.findById(postId)
                .orElseThrow(AdminExceptionCode.DID_NOT_EXISTS_TEMPORAL_POST::create);

        AdminExceptionCode.REGISTER_COMPLETE_TAG.invokeByCondition(temporalTechBlogPost.isRegistrationCompleted());

        // replace 이후 중복 카테고리이름에 대한 중복 제거
        final var categories = tagNames.stream()
                .map(this::ifPresetGetOrCreateNewTag)
                .filter(Category::isRegistrationAllowed)
                .distinct()
                .map(category -> new TemporalTechPostTag(category, temporalTechBlogPost))
                .toList();

        var temporalTechPostTags = temporalTechPostTagRepository.saveAll(categories);

        temporalTechBlogPost.registerCategory(temporalTechPostTags);
        temporalTechBlogPost.updateAiSummarizedText(aiSummarizedText);
    }

    private Category ifPresetGetOrCreateNewTag(String name) {
        return categoryRepository.findByName(name)
                .map(category -> category.isReplaceType() ? replaceCategory(category) : category)
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }

    private Category replaceCategory(Category category) {
        return categoryRepository.findById(category.getReferenceId())
                .orElseThrow(AdminExceptionCode.DID_NOT_EXISTS_REFERENCE_ID::create);
    }
}
