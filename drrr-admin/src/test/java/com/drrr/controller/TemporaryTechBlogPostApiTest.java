package com.drrr.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drrr.domain.techblogpost.service.RegisterPostTagService;
import com.drrr.domain.techblogpost.service.SearchTemporaryTechBlogPostService;
import com.drrr.temporarytechblogpost.controller.TemporaryTechBlogPostApi;
import com.drrr.util.ApiTest;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;


@WebMvcTest(controllers = TemporaryTechBlogPostApi.class)
class TemporaryTechBlogPostApiTest extends ApiTest {

    @InjectMocks
    private TemporaryTechBlogPostApi temporalTechBlogPostApi;
    @MockBean
    private SearchTemporaryTechBlogPostService searchTemporaryTechBlogPostService;
    @MockBean
    private RegisterPostTagService registerPostTagService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void 기간내_요청_범위의_조회가_정상적으로_이루어지는가() throws Exception {
        requestTemporaryTechBlogPostSearchApi("2023-09-01", "2023-09-01")
                .andExpect(status().isOk());
    }

    private ResultActions requestTemporaryTechBlogPostSearchApi(String startDate, String endDate) throws Exception {
        return mockMvc.perform(get("/api/temporary-tech-blog-post")
                        .queryParams(new LinkedMultiValueMap<>() {{
                            this.put("startDate", Collections.singletonList(startDate));
                            this.put("lastDate", Collections.singletonList(endDate));
                        }}))
                .andDo(print());
    }


    @Test
    void 시작일이_마지막일보다_더_뒤에_위치하는_경우_에러가_발생합니다() throws Exception {
        requestTemporaryTechBlogPostSearchApi("2023-09-01", "2023-08-01")
                .andExpect(status().isBadRequest());
    }


    @Test
    void 게시글_태그_등록_기능은_파라미터를_필수적으로_요구합니다() throws Exception {
        mockMvc.perform(patch("/api/temporary-tech-blog-post/1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void 게시글_조회_쿼리에서_코드가_정상적으로_입력됩니다() throws Exception {
        mockMvc.perform(get("/api/temporary-tech-blog-post/sample?code=100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}