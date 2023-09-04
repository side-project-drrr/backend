package com.drrr.domain.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.drrr.core.exception.admin.AdminException;
import com.drrr.core.exception.admin.AdminExceptionCode;
import com.drrr.domain.admin.entity.Admin;
import com.drrr.domain.admin.repository.AdminRepository;
import com.drrr.domain.admin.service.AdminSignInService.AdminSignInDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class AdminSignInServiceTest {
    @Autowired
    private AdminSignInService adminSignInService;

    @Autowired
    private AdminRepository adminRepository;


    @Test
    void 로그인이_정상적으로_동작합니다() {
        final String loginId = "loginId";
        final String password = "password";

        final Admin admin = Admin.builder()
                .loginId(loginId)
                .password(password)
                .build();
        final AdminSignInDto adminSignInDto = AdminSignInDto.builder()
                .loginId(loginId)
                .password(password)
                .build();

        adminRepository.save(admin);

        final Long id = adminSignInService.execute(adminSignInDto);

        final Admin assertAdmin = adminRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        assertThat(assertAdmin.getLoginId()).isEqualTo(loginId);
    }

    @Test
    void 로그인_검증을_실패합니다() {
        final String loginId = "loginId";
        final String password = "password";
        final String otherPassword = "otherPassword";
        final AdminException assertAdminException = AdminExceptionCode.FAIL_SIGNIN.invoke();

        final Admin admin = Admin.builder()
                .loginId(loginId)
                .password(password)
                .build();

        final AdminSignInDto adminSignInDto = AdminSignInDto.builder()
                .loginId(loginId)
                .password(otherPassword)
                .build();

        adminRepository.save(admin);

        assertThatThrownBy(() -> adminSignInService.execute(adminSignInDto))
                .isInstanceOf(AdminException.class)
                .satisfies(exception -> {
                    final AdminException adminException = (AdminException) exception;
                    assertThat(adminException).isEqualTo(assertAdminException);
                });
    }

}