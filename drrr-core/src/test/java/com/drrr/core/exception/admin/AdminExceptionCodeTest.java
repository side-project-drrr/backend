package com.drrr.core.exception.admin;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("관리자_에러_테스트")
class AdminExceptionCodeTest {

    @Test
    void 에러가_정상적으로_호출됩니다() {
        assertThatThrownBy(() -> {
            throw AdminExceptionCode.ADMIN.invoke();
        }).isInstanceOf(AdminException.class);
    }

}