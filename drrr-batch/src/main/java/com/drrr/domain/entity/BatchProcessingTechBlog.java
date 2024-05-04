package com.drrr.domain.entity;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
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


    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void replaceCategories(Function<Long, Optional<Category>> categoryReplacer) {
        categories.forEach(postCategory -> postCategory.replaceCategory(categoryReplacer));
        categories.removeIf(
                distinctByKey(postCategory -> postCategory.getPostId() + ":" + postCategory.getCategoryId()));
    }


}
