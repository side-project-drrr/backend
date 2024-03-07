package com.drrr.util;

import com.drrr.infra.notifications.kafka.email.EmailProducer;
import com.drrr.web.jwt.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest
public class ControllerUnitTest {
    @MockBean
    protected EmailProducer emailProducer;
    @MockBean
    protected JwtProvider jwtProvider;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
