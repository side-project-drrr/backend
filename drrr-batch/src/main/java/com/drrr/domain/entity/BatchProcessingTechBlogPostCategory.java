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

    private static final String REPLACE_FAIL_MESSAGE_FORMAT = "잘못된 reference id입니다. category id: %s, post id : %s";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techblogpost_id")
    private BatchProcessingTechBlog post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public BatchProcessingTechBlogPostCategory(BatchProcessingTechBlog batchProcessingTechBlog, Category category) {
        this.post = batchProcessingTechBlog;
        this.category = category;
    }


    public Long getReferenceId() {
        return category.getReferenceId();
    }

    public void replaceCategory(Function<Long, Optional<Category>> categoryReplacer) {
        if (!category.isReplaceType()) {
            return;
        }

        // 카테고리 변환이 실패하는 경우 reference id가 잘못된 상태
        this.category = categoryReplacer.apply(getReferenceId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                String.format(REPLACE_FAIL_MESSAGE_FORMAT, getCategory(), getReferenceId()))
                );
    }


    /**
     * 정규화되지 않은 카테고리 이름 여부를 판단하는 메서드
     *
     * <p>
     * 정상적인 카테고리 명 ex) "ELK (ElasticSearch, k...)"
     * <p>
     * 비정상적인 카테고리 명 ex) "Java, Node, Spring"
     */
    public boolean isUnNormalizedCategoryName() {
        var categoryName = this.getCategoryName();

        boolean containsComma = categoryName.contains(",");
        boolean containsParentheses = categoryName.contains("(") && categoryName.contains(")");
        boolean commaAfterParentheses = containsParentheses && categoryName.indexOf(",") > categoryName.indexOf(")");

        return containsComma && (!containsParentheses || commaAfterParentheses);
    }


    public String getCategoryName() {
        return category.getName();
    }

    public boolean isExtendType() {
        return category.isExtendType();
    }


}
