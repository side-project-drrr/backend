package com.drrr.domain.entity;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "DRRR_TECHBLOGPOST_CATEGORY")
public class BatchProcessingTechBlogPostCategory extends BaseEntity {

    private static final String REPLACE_FAILE_MESSAGE_FORMAT = "잘못된 reference id입니다. category id: %s, post id : %s";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techblogpost_id")
    private BatchProcessingTechBlog post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    public Long getReferenceId() {
        return category.getReferenceId();
    }

    public Long getCategoryId() {
        return category.getId();
    }

    public Long getPostId() {
        return post.getId();
    }

    public void replaceCategory(
            Function<Long, Optional<Category>> categoryReplacer
    ) {
        if (category.isReplaceType()) {
            log.info("replace 처리");

            categoryReplacer.apply(getReferenceId())
                    .orElseThrow(() -> new IllegalStateException(String.format(REPLACE_FAILE_MESSAGE_FORMAT,
                                    getCategory(),
                                    getReferenceId())
                            )
                    );
        }
    }


}
