package com.drrr.techblogpost;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.core.exception.member.MemberExceptionCode;
import com.drrr.core.exception.techblog.TechBlogExceptionCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.like.repository.TechBlogPostLikeRepository;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

/**
 * <h3>Given</h3>
 * <h2>Member Id M1~M500 생성</h2>
 * <br>Member Id M1~M500이 선호하는 카테고리 C3, C5, C7</br>
 * <br>Member Id M1~M500이 추가적으로 읽은 카테고리 C2, C8</br>
 * <br>Member Id M1~M500의 가중치 값 C2-8, C3-3, C5-4, C7-2, C8-2</br>
 *
 * <h2>Post 생성</h2>
 * <br>Post Id P1~P100까지 생성</br>
 * <br>Post Id 100 -> 좋아요 수 500개
 *
 * <h2>Category 생성</h2>
 * <br>Category Id C1~C10까지 생성</br>
 *
 * <h2>Category별 가중치 값 생성</h2>
 * <br>[C2-(8.0)], [C3-(3.0)], [C5-(4.0)], [C7-(2.0)], [C8-(2.0)]
 *
 * <h2>Member Id : 1~500 이 읽은 Post 목록 및 카테고리 생성</h2>
 * <br>Post Id 목록 : P1, P3, P5, P7, P9</br>
 * <br>[P1-C3,C5,C7], [P3-C2,C3,C7], [P5-C9], [P7-C4,C6,C9], [P9-C1,C2,C3]</br>
 * <br>그 외 나머지 Post는 P(2,4,6,8,10)-C2,C3,C5,C7,C8를 가지고 있고 P(11)~P(50) C-8로 통일</br>
 *
 * <h2>추천 받아야 하는 Post Id 기댓값</h2>
 * <br>P2, P4, P6, P8, P10</br>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TechBlogPostE2ETest {
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
    private MemberPostLogRepository memberTechBlogPostRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    @Autowired
    private TechBlogPostLikeRepository techBlogPostLikeRepository;
    @Autowired
    private TechBlogPostService techBlogPostService;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        databaseCleaner.clear();
        //M1~M500 생성
        List<Member> members = IntStream.rangeClosed(1, 500).mapToObj(i -> {
            String email = "user" + i + "@example.com";
            String nickname = "user" + i;
            String provider = "provider" + i;
            String providerId = "providerId" + i;
            String imageUrl = "http://example.com/image" + i + ".jpg";
            return Member.createMember(email, nickname, provider, providerId, imageUrl);
        }).collect(Collectors.toList());
        memberRepository.saveAll(members);

        TechBlogPost postLike = TechBlogPost.builder()
                .writtenAt(LocalDate.now())
                .author("author")
                .thumbnailUrl("thumbnail")
                .title("title")
                .summary("summary")
                .aiSummary("aiSummary")
                .urlSuffix("urlSuffix")
                .url("url")
                .like(500)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build();

        //P1~P99 생성
        List<TechBlogPost> techBlogPosts = IntStream.rangeClosed(1, 99).mapToObj(i -> {
            //현재로부터 몇년전까지 랜덤으로 연월일을 뽑을지 정함
            LocalDate createdDate = LocalDate.of(2023, 9, 30);
            createdDate.minusDays(i);
            String author = "Author" + i; // 짝수 인덱스에서만 저자 설정
            String thumbnailUrl = "http://example.com/thumbnail" + i + ".jpg";
            String title = "Title" + i;
            String summary = (i % 3 == 0) ? "Summary" + i : null; // 3의 배수 인덱스에서만 요약 설정
            String aiSummary = (i % 3 == 0) ? "Summary" + i : null; // 3의 배수 인덱스에서만 요약 설정
            String urlSuffix = "/suffix/" + i;
            String url = "http://example.com/suffix/" + i;
            TechBlogCode techBlogCode = TechBlogCode.values()[i
                    % TechBlogCode.values().length]; // 순환적으로 TechBlogCode 값 할당
            return TechBlogPost.builder()
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
        }).collect(Collectors.toList());

        techBlogPosts.add(postLike);
        techBlogPostRepository.saveAll(techBlogPosts);

        //PL1 생성
        List<TechBlogPostLike> techBlogPostLikes = IntStream.range(0, 500).mapToObj(i -> {
            List<Member> memberList = memberRepository.findAll();

            if (memberList.isEmpty()) {
                throw MemberExceptionCode.MEMBER_NOT_FOUND.newInstance();
            }

            return TechBlogPostLike.builder()
                    .member(memberList.get(i))
                    .post(postLike)
                    .build();
        }).toList();

        techBlogPostLikeRepository.saveAll(techBlogPostLikes);

        //C1~C10 생성
        List<Category> categories = IntStream.rangeClosed(1, 10).mapToObj(i -> {
            String categoryName = "Category" + i;
            String categoryDisplayName = "Display Category" + i;
            return Category.builder()
                    .name(categoryName)
                    .build();
        }).collect(Collectors.toList());
        categoryRepository.saveAll(categories);

        //각 M1~M500의 CW 생성
        List<Member> memberList = memberRepository.findAll();
        List<Category> categoryWeights = categoryRepository.findIds(Arrays.asList(2L, 3L, 5L, 7L, 8L));
        List<Double> weights = Arrays.asList(8.0, 3.0, 4.0, 2.0, 2.0);
        List<CategoryWeight> categoryWeightList = new ArrayList<>();
        IntStream.range(0, 500).forEach(j -> {
            IntStream.range(0, categoryWeights.size()).forEach(i -> {
                Category category = categoryWeights.get(i);
                double value = weights.get(i);
                boolean preferred = i == 3 || i == 5 || i == 7;

                categoryWeightList.add(CategoryWeight.builder()
                        .member(memberList.get(j))
                        .category(category)
                        .value(value)
                        .lastReadAt(LocalDateTime.now())
                        .preferred(preferred)
                        .build());
            });
        });

        categoryWeightRepository.saveAll(categoryWeightList);

        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        List<Category> categoryList = categoryRepository.findAll();
        List<TechBlogPostCategory> techBlogPostCategories = new ArrayList<>();

        //M1~M500의 Log 생성
        List<MemberPostLog> logs = new ArrayList<>();
        IntStream.range(0, 500).forEach(i -> {
            logs.add(MemberPostLog.builder()
                    .postId(1L)
                    .memberId(memberList.get(i).getId())
                    .isRecommended(false)
                    .isRead(true)
                    .build());
            logs.add(MemberPostLog.builder()
                    .postId(3L)
                    .memberId(memberList.get(i).getId())
                    .isRecommended(false)
                    .isRead(true)
                    .build());
            logs.add(MemberPostLog.builder()
                    .postId(5L)
                    .memberId(memberList.get(i).getId())
                    .isRecommended(false)
                    .isRead(true)
                    .build());
            logs.add(MemberPostLog.builder()
                    .postId(7L)
                    .memberId(memberList.get(i).getId())
                    .isRecommended(false)
                    .isRead(true)
                    .build());
            logs.add(MemberPostLog.builder()
                    .postId(9L)
                    .memberId(memberList.get(i).getId())
                    .isRecommended(false)
                    .isRead(true)
                    .build());
        });

        memberTechBlogPostRepository.saveAll(logs);

        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(0))
                .category(categoryList.get(2))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(0))
                .category(categoryList.get(4))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(0))
                .category(categoryList.get(6))
                .build());

        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(2))
                .category(categoryList.get(1))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(2))
                .category(categoryList.get(2))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(2))
                .category(categoryList.get(6))
                .build());

        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(4))
                .category(categoryList.get(8))
                .build());

        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(6))
                .category(categoryList.get(3))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(6))
                .category(categoryList.get(5))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(6))
                .category(categoryList.get(8))
                .build());

        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(8))
                .category(categoryList.get(0))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(8))
                .category(categoryList.get(1))
                .build());
        techBlogPostCategories.add(TechBlogPostCategory.builder()
                .post(posts.get(8))
                .category(categoryList.get(2))
                .build());

        IntStream.rangeClosed(1, 7).forEach(j -> {
            Category category = categoryList.get(j);
            techBlogPostCategories.add(TechBlogPostCategory.builder()
                    .post(posts.get(1))
                    .category(category)
                    .build());
        });
        IntStream.rangeClosed(1, 7).forEach(j -> {
            Category category = categoryList.get(j);
            techBlogPostCategories.add(TechBlogPostCategory.builder()
                    .post(posts.get(3))
                    .category(category)
                    .build());
        });
        IntStream.rangeClosed(1, 7).forEach(j -> {
            Category category = categoryList.get(j);
            techBlogPostCategories.add(TechBlogPostCategory.builder()
                    .post(posts.get(5))
                    .category(category)
                    .build());
        });
        IntStream.rangeClosed(1, 7).forEach(j -> {
            Category category = categoryList.get(j);
            techBlogPostCategories.add(TechBlogPostCategory.builder()
                    .post(posts.get(7))
                    .category(category)
                    .build());
        });
        IntStream.rangeClosed(1, 7).forEach(j -> {
            Category category = categoryList.get(j);
            techBlogPostCategories.add(TechBlogPostCategory.builder()
                    .post(posts.get(9))
                    .category(category)
                    .build());
        });

        IntStream.rangeClosed(10, 49).forEach(i -> {
            TechBlogPost post = posts.get(i);
            Category category = categoryList.get(7);
            techBlogPostCategories.add(TechBlogPostCategory.builder()
                    .post(post)
                    .category(category)
                    .build());

        });

        techBlogPostCategoryRepository.saveAll(techBlogPostCategories);
    }

    @Test
    void 여러_사용자가_게시물에_좋아요를_누르면_정상적으로_증가합니다() throws InterruptedException {
        //when
        String accessToken = jwtProvider.createAccessToken(1L, Instant.now());
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(500);

        IntStream.rangeClosed(1, 500).forEach(i -> {

            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .body("""
                            {
                                "memberId": """ + i + """
                                ,"postId" : 1
                            }
                            """)
                    .post("/api/v1/post/like");
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();

        });
        latch.await();
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        //then
        TechBlogPost techBlogPost = techBlogPostRepository.findById(1L).orElseThrow(() -> {
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        });

        int likeCount = techBlogPost.getPostLike();
        assertThat(likeCount).isEqualTo(500);

    }

    @Test
    void 여러_사용자가_게시물에_좋아요를_누르면_정상적으로_감소합니다() throws InterruptedException {
        //when
        String accessToken = jwtProvider.createAccessToken(1L, Instant.now());
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(500);

        IntStream.rangeClosed(1, 500).forEach(i -> {

            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .body("""
                            {
                                "memberId": """ + i + """
                                ,"postId" : 100
                            }
                            """)
                    .delete("/api/v1/post/like");
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();

        });
        latch.await();
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        //then
        TechBlogPost techBlogPost = techBlogPostRepository.findById(100L).orElseThrow(() -> {
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        });

        int likeCount = techBlogPost.getPostLike();
        assertThat(likeCount).isEqualTo(0);

    }

    @Test
    void 모든_게시물을_정상적으로_가져옵니다() throws JsonProcessingException {
        //when
        List<TechBlogPost> posts =
                new ObjectMapper().readValue(
                        given()
                                .log().all()
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .get("/api/v1/posts")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(),
                        new TypeReference<List<TechBlogPost>>() {
                        }
                );

        //then
        List<TechBlogPost> postsFromDb = techBlogPostRepository.findAll();
        if (posts.isEmpty()) {
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }

        assertThat(postsFromDb.size()).isEqualTo(posts.size());
        assertThat(postsFromDb)
                .zipSatisfy(posts, (actual, expected) -> {
                    assertThat(actual.getId()).isEqualTo(expected.getId());
                    // 필요하다면 추가적인 필드 비교 로직을 여기에 작성
                });
    }

    @Test
    void 특정_카테고리에_해당하는_게시물을_정상적으로_가져옵니다() throws JsonProcessingException {
        //when
        List<TechBlogPost> posts =
                new ObjectMapper().readValue(
                        given()
                                .log().all()
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .get("/api/v1/posts/category/{id}", 8)
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(),
                        new TypeReference<List<TechBlogPost>>() {
                        }
                );

        //then
        List<TechBlogPost> postsFromDb = techBlogPostService.findPostsByCategory(8L);
        if (posts.isEmpty()) {
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }

        assertThat(postsFromDb.size()).isEqualTo(posts.size());
        assertThat(postsFromDb)
                .zipSatisfy(posts, (actual, expected) -> {
                    assertThat(actual.getId()).isEqualTo(expected.getId());
                    // 필요하다면 추가적인 필드 비교 로직을 여기에 작성
                });
    }
}
