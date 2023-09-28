package com.drrr.domain.admin.entity;


import com.drrr.core.exception.admin.AdminExceptionCode;
import com.drrr.domain.jpa.entity.BaseEntity;
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
@Table(name = "DRRR_ADMIN")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "ADMIN_ID")
public class Admin extends BaseEntity {
    private String loginId;
    private String password;


    @Builder
    protected Admin(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public void validateSamePassword(String password) {
        if (!Objects.equals(password, this.password)) {
            throw AdminExceptionCode.FAIL_SIGNIN.invoke();
        }
    }

}
