package com.drrr.admin.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drrr.admin.payload.request.AdminSignInRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(AdminAuthenticationApi.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("관리자 로그인 api 단위 테스트")
class AdminAuthenticationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    // happy test
    @Test
    void 관리자_로그인_요청이_정상적으로_처리된다() throws Exception {
        requestSignInApi(null)
                .andExpectAll(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {7, 13})
    void 관리자_로그인_요청에서_아이디가_8자리_이상_12자리_미만인_경우_에러가_발생합니다(int size) throws Exception {
        final String loginId = "a".repeat(size);
        AdminSignInRequest adminSignInRequest = AdminSignInRequest.builder()
                .loginId(loginId)
                .password("password1234")
                .build();
        requestSignInApi(adminSignInRequest);
    }

    private ResultActions requestSignInApi(AdminSignInRequest adminSignInRequest) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsBytes(adminSignInRequest)))
                    .andDo(print());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {7, 21})
    void 관리자_로그인_요청에서_패스워드가_8자리_미만_20자리_초과인_경우_에러가_발생합니다(int size) throws Exception {
        final String generatedPassword = "a".repeat(size);

        AdminSignInRequest adminSignInRequest = AdminSignInRequest.builder()
                .loginId("admin1234")
                .password(generatedPassword)
                .build();

        requestSignInApi(adminSignInRequest)
                .andExpectAll(status().isBadRequest());
    }


}