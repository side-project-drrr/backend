package com.drrr.category;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.exception.category.CategoryExceptionCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.service.CategoryService.CategoryDto;
import com.drrr.util.DatabaseCleaner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CategoryE2ETest {

    @LocalServerPort
    int port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void 가짜_데이터_삽입() {
        RestAssured.port = port;
        databaseCleaner.clear();
        insertCategoryDummyData();
    }


    private void insertCategoryDummyData() {
        int CATEGORY_COUNT = 20;
        List<Category> categories = IntStream.rangeClosed(1, CATEGORY_COUNT).mapToObj(i -> {
            String categoryDisplayName = "Display Category" + i;
            return Category.builder()
                    .name(categoryDisplayName)
                    .build();
        }).collect(Collectors.toList());
        categoryRepository.saveAll(categories);
    }

    @Test
    void 모든_카테고리를_정상적으로_가져옵니다() throws JsonProcessingException {
        //when

        List<CategoryDto> categoryDtos =
                new ObjectMapper().readValue(
                        given()
                                .log().all()
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .get("/api/v1/categories")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(),
                        new TypeReference<List<CategoryDto>>() {
                        }
                );

        //then
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }
        categories = categories.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).toList();

        assertThat(categories.size()).isEqualTo(categoryDtos.size());
        assertThat(categories)
                .zipSatisfy(categoryDtos, (actual, expected) -> {
                    assertThat(actual.getName()).isEqualTo(expected.categoryName());
                    // 필요하다면 추가적인 필드 비교 로직을 여기에 작성
                });
    }

    @Test
    void 특정_카테고리를_정상적으로_가져옵니다() throws JsonProcessingException {
        //when
        String ids = String.join(",", "1", "2", "3", "4", "5");
        List<CategoryDto> categoryDtos =
                new ObjectMapper().readValue(
                        given()
                                .log().all()
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .get("/api/v1/categories/selection?ids=" + ids)
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(),
                        new TypeReference<List<CategoryDto>>() {
                        }
                );

        //then
        List<Category> categories = categoryRepository.findByIdIn(Arrays.asList(1L, 2L, 3L, 4L, 5L));
        if (categories.isEmpty()) {
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }
        categories = categories.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).toList();

        assertThat(categories.size()).isEqualTo(categoryDtos.size());
        assertThat(categories)
                .zipSatisfy(categoryDtos, (actual, expected) -> {
                    assertThat(actual.getName()).isEqualTo(expected.categoryName());
                    // 필요하다면 추가적인 필드 비교 로직을 여기에 작성
                });
    }
}
