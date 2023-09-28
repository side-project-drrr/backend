package com.drrr.domain.category.entity;


import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
@Table(name = "DRRR_CATEGORY")
@PrimaryKeyJoinColumn(name = "CATEGORY_ID")
public class Category extends BaseEntity {

    private String uniqueName;
    private String categoryDisplayName;


}
