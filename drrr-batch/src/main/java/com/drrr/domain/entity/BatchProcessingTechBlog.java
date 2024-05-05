package com.drrr.domain.entity;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRRR_TECHBLOGPOST")
public class BatchProcessingTechBlog extends BaseEntity {


    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BatchProcessingTechBlogPostCategory> categories;


    @Transient
    private List<BatchProcessingTechBlogPostCategory> unNormalizedPostCategory;

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) != null;
    }

    public void replaceCategories(Function<Long, Optional<Category>> categoryReplacer) {
        this.categories.forEach(postCategory -> postCategory.replaceCategory(categoryReplacer));
        this.categories.removeIf(distinctByKey(BatchProcessingTechBlogPostCategory::getCategoryName));
    }


    public List<Category> executeNormalize() {
        final var unNormalizedPostCategory = categories.stream()
                .filter(BatchProcessingTechBlogPostCategory::isUnNormalizedCategoryName)
                .toList();

        if (unNormalizedPostCategory.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("정규화된 처리 전 카테고리 이름 {}",
                unNormalizedPostCategory.stream()
                        .map(BatchProcessingTechBlogPostCategory::getCategoryName)
                        .collect(Collectors.joining("\n"))
        );

        this.unNormalizedPostCategory = unNormalizedPostCategory;

        return unNormalizedPostCategory.stream()
                .map(BatchProcessingTechBlogPostCategory::getCategoryName)
                .map(str -> str.split(","))
                .flatMap(names -> Arrays.stream(names)
                        .map(String::trim)
                        .map(Category::new)
                )
                .toList();
    }

    /**
     * 새로운 카테고리 등록하면서 기존 정규화되지 않은 카테고리를 삭제합니다.
     *
     * @param newCategories
     */

    public void register(List<Category> newCategories) {
        this.categories.removeAll(unNormalizedPostCategory);

        newCategories.stream()
                .map(category -> new BatchProcessingTechBlogPostCategory(this, category))
                .forEach(categories::add);

        this.categories.removeIf(distinctByKey(BatchProcessingTechBlogPostCategory::getCategoryName));
    }


    public void extendCategories(Function<Long, Optional<Category>> categoryReplacer) {
        categories.addAll(categories.stream()
                .filter(BatchProcessingTechBlogPostCategory::isExtendType)
                .map(BatchProcessingTechBlogPostCategory::getReferenceId)
                .map(categoryReplacer)
                .map(Optional::orElseThrow)
                .map(category -> new BatchProcessingTechBlogPostCategory(this, category))
                .toList());

        categories.removeIf(distinctByKey(BatchProcessingTechBlogPostCategory::getCategoryName));
    }
}
