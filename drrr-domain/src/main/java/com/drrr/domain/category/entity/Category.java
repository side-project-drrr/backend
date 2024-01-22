package com.drrr.domain.category.entity;


import com.drrr.domain.jpa.entity.BaseEntity;
import com.drrr.domain.metacategory.domain.MetaCategoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "DRRR_CATEGORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "CATEGORY_ID")
public class Category extends BaseEntity {

    @Column(name = "name", unique = true)
    private String name;

    private MetaCategoryType metaCategoryType;

    private Long referenceId;

    @Builder
    public Category(String name) {
        this.name = name;
        this.metaCategoryType = MetaCategoryType.NONE;
    }

    public boolean isReplaceType() {
        return metaCategoryType == MetaCategoryType.REPLACE;
    }

    public void changeIgnoreType() {
        this.metaCategoryType = MetaCategoryType.IGNORE;
    }

    public boolean isRegistrationAllowed() {
        return metaCategoryType != MetaCategoryType.IGNORE;
    }

    public void changeReplaceType(Long id) {
        this.metaCategoryType = MetaCategoryType.REPLACE;
        this.referenceId = id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Category category = (Category) object;
        return Objects.equals(getName(), category.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
