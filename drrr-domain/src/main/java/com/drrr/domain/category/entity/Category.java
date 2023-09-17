package com.drrr.domain.category.entity;


import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "DRRR_CATEGORY")
@PrimaryKeyJoinColumn(name = "CATEGORY_ID")
public class Category extends BaseEntity {

    private String uniqueName;
    private String categoryDisplayName;


}
