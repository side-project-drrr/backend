package com.drrr.auth.controller;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.service.impl.ExchangeOAuth2AccessTokenService;
import com.drrr.auth.service.impl.IssuanceTokenService;
import com.drrr.auth.service.impl.SignInService;
import com.drrr.auth.service.impl.SignUpService;
import com.drrr.auth.service.impl.UnregisterService;
import com.drrr.util.ControllerUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerUnitTest {

    @MockBean
    SignUpService signUpService;
    @MockBean
    SignInService signInService;
    @MockBean
    UnregisterService unregisterService;
    @MockBean
    IssuanceTokenService issuanceTokenService;
    @MockBean
    ExchangeOAuth2AccessTokenService exchangeOAuth2AccessTokenService;


    @Test
    @WithMockUser
    void OAuthProfile_api_요청_바디에_요구하는_값이_들어오는지_검증합니다() throws Exception {
        mockMvc.perform(get("/api/v1/auth/oauth2/profile"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser()
    void 회원가입_api_요청바디_값_검증() throws Exception {
        final var signupRequest = SignUpRequest.builder().build();
        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


}