package com.drrr.category;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
import com.drrr.web.page.request.PageableRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CategoryE2ETest {

    @LocalServerPort
    int port;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryWeightRepository categoryWeightRepository;
    @Autowired
    private JwtProvider jwtProvider;

    private final static int CATEGORY_WEIGHT_COUNT = 3;

    @Autowired
    private DatabaseCleaner databaseCleaner;
    private static final int CATEGORY_COUNT = 10;
    private static final String[] CATEGORY_NAMES = {
            "Python",
            "Java",
            "Spring",
            "JPA",
            "JavaScript",
            "TypeScript",
            "React",
            "Vue",
            "Angular",
            "Node.js"
    };

    @BeforeEach
    public void 가짜_데이터_삽입() {
        RestAssured.port = port;
        databaseCleaner.clear();

        String email = "user@example.com";
        String nickname = "user";
        String provider = "provider";
        String providerId = "providerId";
        String imageUrl = "http://example.com/image.jpg";
        Member member = Member.createMember(email, nickname, provider, providerId, imageUrl);
        memberRepository.save(member);

        List<Category> categories = IntStream.range(0, CATEGORY_COUNT).mapToObj(i -> {
            return Category.builder()
                    .name(CATEGORY_NAMES[i])
                    .build();
        }).collect(Collectors.toList());
        categoryRepository.saveAll(categories);

        List<CategoryWeight> categoryWeightList = new ArrayList<>();
        IntStream.range(0, CATEGORY_WEIGHT_COUNT).forEach(i -> {
            categoryWeightList.add(CategoryWeight.builder()
                    .member(member)
                    .category(categories.get(i))
                    .weightValue(1.0)
                    .lastReadAt(LocalDateTime.now())
                    .preferred(true)
                    .build());
        });
        categoryWeightRepository.saveAll(categoryWeightList);

        LocalDate createdDate = LocalDate.now();
        String author = "Author"; // 짝수 인덱스에서만 저자 설정
        String thumbnailUrl = "http://example.com/thumbnail.jpg";
        String title = "Title";
        String summary = "Summary"; // 3의 배수 인덱스에서만 요약 설정
        String aiSummary = "Ai Summary"; // 3의 배수 인덱스에서만 요약 설정
        String urlSuffix = "/suffix/";
        String url = "http://example.com/suffix/";
        TechBlogCode techBlogCode = TechBlogCode.KAKAO; // 순환적으로 TechBlogCode 값 할당
        TechBlogPost post = TechBlogPost.builder()
                .writtenAt(createdDate)
                .author(author)
                .thumbnailUrl(thumbnailUrl)
                .title(title)
                .summary(summary)
                .aiSummary(aiSummary)
                .urlSuffix(urlSuffix)
                .url(url)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build();
        techBlogPostRepository.save(post);

        List<TechBlogPostCategory> techBlogPostCategories = IntStream.range(0, 3).mapToObj(i -> {
            return techBlogPostCategoryRepository.save(TechBlogPostCategory.builder()
                    .category(categories.get(i))
                    .post(post)
                    .build());
        }).toList();
        techBlogPostCategoryRepository.saveAll(techBlogPostCategories);


    }

    @Test
    public void 사용자가_가장_많이_선호하는_탑_카테고리를_잘_가져옵니다() throws JsonProcessingException {
        //when
        Response response = given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .get("/api/v1/top/categories/{count}", 3)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<CategoryDto> content = response.jsonPath().getList("", CategoryDto.class);
        //then
        assertThat(content.stream().map(item -> item.name())).containsExactlyInAnyOrder("Python", "Java", "Spring");
        assertThat(content).size().isEqualTo(3);
    }

    @Test
    public void 사용자의_선호_카테고리를_잘_가져옵니다() throws JsonProcessingException {
        //when
        String accessToken = jwtProvider.createAccessToken(1L, Instant.now());
        Response response = given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + accessToken)
                .get("/api/v1/members/me/category-preference")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<CategoryDto> content = response.jsonPath().getList("", CategoryDto.class);
        //then
        assertThat(content.stream().map(item -> item.name())).containsExactlyInAnyOrder("Python", "Java", "Spring");
        assertThat(content).size().isEqualTo(3);
    }

    @Test
    public void 특정_기술블로그에_해당하는_카테고리를_잘_가져옵니다() throws JsonProcessingException {
        //given
        String index = "J";

        //when
        Response response = given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .get("/api/v1/categories/posts/{postId}", 1L)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<CategoryDto> content = response.jsonPath().getList("", CategoryDto.class);
        //then
        assertThat(content.stream().map(item -> item.name())).containsExactlyInAnyOrder("Python", "Java", "Spring");
        assertThat(content).size().isEqualTo(3);
    }

    @Test
    public void 모든_카테고리를_잘_가져옵니다() throws JsonProcessingException {
        //given
        String index = "J";

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("page", "0");
        params.put("size", "10");

        Response response = given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .queryParams(params)
                .get("/api/v1/categories/all")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<CategoryDto> content = response.jsonPath().getList("content", CategoryDto.class);
        //then
        assertThat(content.stream().map(item -> item.name())).containsExactlyInAnyOrder("Python",
                "Java",
                "Spring",
                "JPA",
                "JavaScript",
                "TypeScript",
                "React",
                "Vue",
                "Angular",
                "Node.js");
        assertThat(content).size().isEqualTo(10);
    }

    @Test
    public void 카테고리_키워드_검색이_잘_작동합니다() throws JsonProcessingException {
        //when
        Map<String, PageableRequest> params = new HashMap<>();
        PageableRequest pageableRequest = PageableRequest.builder()
                .page(0)
                .size(10)
                .build();
        params.put("pageable", pageableRequest);
        String keyword = "J";

        Response response = given()
                .log().all()
                .when()
                .queryParam("keyword", keyword)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .contentType(ContentType.APPLICATION_JSON.toString())
                .get("/api/v1/categories/keyword-search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<CategoryDto> content = response.jsonPath().getList("content", CategoryDto.class);
        //then
        assertThat(content.stream().map(item -> item.name())).containsExactlyInAnyOrder("Java", "JavaScript", "JPA");
        assertThat(content).size().isEqualTo(3);
    }

    @Test
    public void 인덱스에_해당하는_카테고리를_잘_가져옵니다() throws JsonProcessingException {
        //given
        String index = "J";

        //when
        Map<String, String> params = new HashMap<>();
        params.put("page", "0");
        params.put("size", "10");
        params.put("language", "ENGLISH");
        params.put("index", index);

        Response response = given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .queryParams(params)
                .get("/api/v1/categories/index-search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<CategoryDto> content = response.jsonPath().getList("content", CategoryDto.class);
        //then
        assertThat(content.stream().map(item -> item.name())).containsExactlyInAnyOrder("Java", "JavaScript", "JPA");
        assertThat(content).size().isEqualTo(3);
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
                                .get("/api/v1/categories?ids=" + ids)
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(),
                        new TypeReference<List<CategoryDto>>() {
                        }
                );

        //then
        List<Category> categories = categoryRepository.findByIdIn(Arrays.asList(1L, 2L, 3L, 4L, 5L));
        if (categories.isEmpty()) {
            throw DomainExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }
        categories = categories.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).toList();

        assertThat(categories.size()).isEqualTo(categoryDtos.size());
        assertThat(categories)
                .zipSatisfy(categoryDtos, (actual, expected) -> {
                    assertThat(actual.getName()).isEqualTo(expected.name());
                    // 필요하다면 추가적인 필드 비교 로직을 여기에 작성
                });
    }

}
