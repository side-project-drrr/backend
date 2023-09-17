package com.drrr.domain.category.entity;


import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
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

    @Column(name = "unique_name", unique = true)
    private String uniqueName;


    @Column(name = "display_name", unique = true)
    private String displayName;


    @Builder
    public Category(String uniqueName, String displayName) {
        this.uniqueName = uniqueName;
        this.displayName = displayName;
    }
}
