package com.drrr.category;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.exception.category.CategoryExceptionCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
import io.restassured.RestAssured;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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
    private final int CATEGORY_COUNT = 20;
    private final int MEMBER_COUNT = 500;
    private final int POST_COUNT = 500;
    private final int CATEGORY_WEIGHT_COUNT = 100;
    private final int MEMBER_POST_LOG_COUNT = 100;
    private final int CATEGORIES_PER_POST = 8;
    private final int MAX_PREFER_CATEGORIES_COUNT = 8;
    @LocalServerPort
    int port;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryWeightRepository categoryWeightRepository;
    @Autowired
    private MemberPostLogRepository memberPostLogRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void 가짜_데이터_삽입() {
        RestAssured.port = port;
        databaseCleaner.clear();
        insertCategoryDummyData();
    }


    private void insertCategoryDummyData() {
        List<Category> categories = IntStream.rangeClosed(1, CATEGORY_COUNT).mapToObj(i -> {
            String categoryDisplayName = "Display Category" + i;
            return Category.builder()
                    .name(categoryDisplayName)
                    .build();
        }).collect(Collectors.toList());
        categoryRepository.saveAll(categories);
    }

    @Test
    void 모든_카테고리를_정상적으로_가져옵니다() throws InterruptedException {
        //when

        given().log()
                .all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .get("/api/v1/categories")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        //then
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        assertThat(categories.size()).isEqualTo(CATEGORY_COUNT);
        assertThat(categories)
                .extracting(category -> category.getId())
                .containsExactlyInAnyOrder(LongStream.rangeClosed(1, 20).boxed().toArray(Long[]::new));
    }

    @Test
    void 특정_카테고리를_정상적으로_가져옵니다() throws InterruptedException {
        //when
        String ids = String.join(",", "1", "2", "3", "4", "5");
        given().urlEncodingEnabled(false).log()
                .all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .get("/api/v1/categories/selection?ids=" + ids)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        //then
        List<Category> categories = categoryRepository.findByIdIn(Arrays.asList(1L, 2L, 3L, 4L, 5L));
        if (categories.isEmpty()) {
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        assertThat(categories.size()).isEqualTo(5);
        assertThat(categories)
                .extracting(category -> category.getId())
                .containsExactlyInAnyOrder(LongStream.rangeClosed(1, 5).boxed().toArray(Long[]::new));
    }
}
