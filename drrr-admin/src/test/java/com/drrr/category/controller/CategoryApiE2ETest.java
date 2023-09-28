package com.drrr.category.controller;

import static io.restassured.RestAssured.given;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import io.restassured.RestAssured;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class CategoryApiE2ETest {

    @LocalServerPort
    int port;


    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void 카테고리가_정상적으로_조회됩니다() {
        categoryRepository.saveAll(IntStream.rangeClosed(1, 9)
                .mapToObj(i -> Category.builder()
                        .uniqueName("JAVA" + i)
                        .displayName("자바" + i)
                        .build())
                .collect(Collectors.toList()));

        given().log()
                .all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .params(new HashMap<>() {{
                    this.put("text", "J");
                }})
                .get("/api/category")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

}