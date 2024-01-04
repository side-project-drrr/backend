package com.drrr.domain.category.service;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchCategoryService {
    private final CategoryRepository categoryRepository;

    public List<SearchCategoryResultDto> execute(final String text, final Pageable pageable) {
        return categoryRepository.findByNameContaining(text, pageable)
                .stream().map(SearchCategoryResultDto::from)
                .toList();
    }

    @Builder
    public record SearchCategoryResultDto(
            Long id,
            String name
    ) {
        public static SearchCategoryResultDto from(final Category category) {
            return SearchCategoryResultDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();
        }
    }


}
