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

    @Column(name = "name", unique = true)
    private String name;


    @Builder
    public Category(String name) {
        this.name = name;
    }
}
