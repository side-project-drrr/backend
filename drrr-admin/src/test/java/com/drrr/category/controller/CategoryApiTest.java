package com.drrr.category.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drrr.domain.category.service.SearchCategoryService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(controllers = CategoryApi.class)
class CategoryApiTest {

    @InjectMocks
    private CategoryApi categoryApi;

    @MockBean
    private SearchCategoryService searchCategoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 카테고리를_조회할때_최소한_한_글자_이상_필요합니다() throws Exception {
        mockMvc.perform(get("/api/category"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    void 카테고리를_조회할때_공백을_제외한_한_글자_이상_필요합니다(final int size) throws Exception {
        final String text = " ".repeat(size);
        mockMvc.perform(get("/api/category")
                        .queryParam("text", text))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 카테고리를_정상적으로_조회합니다() throws Exception {
        mockMvc.perform(get("/api/category")
                        .queryParam("text", "가"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}