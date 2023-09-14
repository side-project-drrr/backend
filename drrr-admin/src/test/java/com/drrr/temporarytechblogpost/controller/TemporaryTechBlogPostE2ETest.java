package com.drrr.temporarytechblogpost.controller;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.drrr.core.code.TechBlogCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.util.DatabaseCleaner;
import io.restassured.RestAssured;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.stream.IntStream;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TemporaryTechBlogPostE2ETest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;


    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        databaseCleaner.clear();
        createTestFixture();
    }

    /**
     * <h3>given</h3>
     * <br> 게시글을 110개 정도 만듭니다.
     * <br> 여기서 당일 크롤링된 게시글은 30개, 하루 전날 크롤링된 게시글은 60개 이틀 전에 크롤링 된 게시글은 20개 입니다.
     * <br>
     */
    void createTestFixture() {
        temporalTechBlogPostRepository.saveAll(IntStream.rangeClosed(1, 30)
                .mapToObj(i -> createTemporalTechBlogPost(0))
                .toList());
        temporalTechBlogPostRepository.saveAll(IntStream.rangeClosed(1, 60)
                .mapToObj(i -> createTemporalTechBlogPost(1))
                .toList());
        temporalTechBlogPostRepository.saveAll(IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createTemporalTechBlogPost(2))
                .toList());

        categoryRepository.saveAll(IntStream.rangeClosed(1, 2).mapToObj(i -> Category.builder()
                .displayName(i + "")
                .uniqueName(i + "")
                .build()).toList());
    }

    private TemporalTechBlogPost createTemporalTechBlogPost(int minusDay) {
        return TemporalTechBlogPost.builder()
                .author("author")
                .createdDate(LocalDate.now())
                .thumbnailUrl(null)
                .title("title")
                .summary("summary")
                .url("")
                .urlSuffix("suffix")
                .techBlogCode(TechBlogCode.WOOWAHAN)
                .crawledDate(LocalDate.now().minusDays(minusDay))
                .build();
    }

    @Test
    void 전체_게시글을_조회할_때_범위_적용이_가능하다() {
        given().log().all()
                .when()
                .queryParams(new HashMap<>() {{
                    this.put("page", 0);
                    this.put("size", 10);
                }})
                .get("/api/temporary-tech-blog-post")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(10))
                .log().all();
    }

    @ParameterizedTest
    @CsvSource(value = {"0:0:30", "1:1:60", "1:0:90"}, delimiter = ':')
    void 전체_게시글을_조회할_때_기간_적용이_가능하다(int before, int after, int result) {
        given().log().all()
                .when()
                .queryParams(new HashMap<>() {{
                    this.put("page", 0);
                    this.put("size", 100);
                    this.put("startDate", LocalDate.now().minusDays(before).toString());
                    this.put("lastDate", LocalDate.now().minusDays(after).toString());
                }})
                .get("/api/temporary-tech-blog-post")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(result))
                .log().all();
    }

    @Test
    void 카테고리가_존재하는_게시글을_가져올_수_있다() {
        given().contentType(ContentType.APPLICATION_JSON.toString())
                .body("""
                        { 
                                        
                             "tagIds":[1,2]
                        }
                        """
                ).log().all().patch("/api/temporary-tech-blog-post/{id}/category", 1L);

        given().log().all()
                .when()
                .queryParams(new HashMap<>() {{
                    this.put("page", 0);
                    this.put("size", 100);
                    this.put("assignTagCompleted", true);
                }})
                .get("/api/temporary-tech-blog-post")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .log().all();
    }


    @Test
    void 임시_기술블로그글의_태그를_등록할_수_있습니다() {
        given().log()
                .all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .body("""
                        {
                            "tagIds": [1,2]
                        }
                        """)
                .patch("/api/temporary-tech-blog-post/{id}/category", 1L)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        final TemporalTechBlogPost temporalTechBlogPost = temporalTechBlogPostRepository.findById(1L).orElseThrow(IllegalArgumentException::new);
        assertThat(temporalTechBlogPost.isRegistrationCompleted()).isTrue();
    }

}