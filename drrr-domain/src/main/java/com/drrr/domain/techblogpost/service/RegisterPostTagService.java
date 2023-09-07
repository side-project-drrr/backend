package com.drrr.domain.techblogpost.service;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 태그 id가 있습니다.");
        }

        final List<TemporalTechPostTag> temporalTechPostTags = categories.stream()
                .map(category -> new TemporalTechPostTag(category, temporalTechBlogPost))
                .toList();

        temporalTechBlogPost.registerCategory(temporalTechPostTagRepository.saveAll(temporalTechPostTags));
    }
}
