package com.drrr.domain.techblogpost.service;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
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

    public void execute(Long postId, List<Long> categoryIds) {
        final TemporalTechBlogPost temporalTechBlogPost = temporalTechBlogPostRepository.findById(postId)
                .orElseThrow(IllegalArgumentException::new);

        final List<Category> categories = categoryRepository.findIds(categoryIds);

        // validate category size
        if (categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 태그 id가 있습니다.");
        }

        // update
        if (temporalTechBlogPost.isRegistrationCompleted()) {
            final List<TemporalTechPostTag> tags = temporalTechBlogPost.getTemporalTechPostTags();

            // 삭제 연산
            final List<TemporalTechPostTag> deleteTags = tags.stream()
                    .filter(temporalTechPostTag -> !categories.contains(temporalTechPostTag.getCategory()))
                    .toList();

            temporalTechPostTagRepository.deleteAll(deleteTags);
            temporalTechBlogPost.removeCategory(deleteTags);
        }

        final List<TemporalTechPostTag> temporalTechPostTags = categories.stream()
                .filter(category -> !temporalTechPostTagRepository.existsByCategoryAndTemporalTechBlogPost(category,
                        temporalTechBlogPost))
                .map(category -> new TemporalTechPostTag(category, temporalTechBlogPost))
                .toList();

        temporalTechBlogPost.registerCategory(temporalTechPostTagRepository.saveAll(temporalTechPostTags));
    }
}
