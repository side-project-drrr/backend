package com.drrr.techblogpost;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.fixture.post.TechBlogPostCategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.fixture.post.TechBlogPostLikeFixture;
import com.drrr.domain.like.repository.TechBlogPostLikeRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.post.RedisPostDynamicDataFixture;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    @Autowired
    private TechBlogPostLikeRepository techBlogPostLikeRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private RedisPostDynamicDataRepository redisPostDynamicDataRepository;


    @BeforeEach
    void setup() {
        RestAssured.port = port;
        databaseCleaner.clear();
        //M1~M500 생성
        List<Member> members = MemberFixture.createMembers(200);
        memberRepository.saveAll(members);

        TechBlogPost post = TechBlogPostFixture.createTechBlogPostLike(200);
        techBlogPostRepository.save(post);

        redisPostDynamicDataRepository.save(
                RedisPostDynamicDataFixture.createRedisPostDynamicData(200, 100, post.getId()));

        techBlogPostLikeRepository.saveAll(
                TechBlogPostLikeFixture.createTechBlogPostLikeIncrease(members.subList(100, 200), post));

        List<Category> categories = CategoryFixture.createCategories(3);
        categoryRepository.saveAll(categories);

        List<TechBlogPostCategory> techBlogPostCategory = TechBlogPostCategoryFixture.createTechBlogPostCategories(
                post,
                categories);
        techBlogPostCategoryRepository.saveAll(
                techBlogPostCategory
        );

        categoryWeightRepository.saveAll(CategoryWeightFixture.createCategoryWeights(members, categories));

    }

    @Test
    void 여러_사용자가_게시물에_좋아요를_누르면_정상적으로_증가합니다() throws InterruptedException {
        //when
        List<TechBlogPost> posts = techBlogPostRepository.findAll();

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        IntStream.rangeClosed(1, 100).forEach(i -> {
            String accessToken = jwtProvider.createAccessToken((long) i, Instant.now());
            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .post("/api/v1/posts/{postId}/like", posts.get(0).getId());
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();

        });
        latch.await();
        executorService.shutdown();

        //then
        TechBlogPost techBlogPost = techBlogPostRepository.findById(posts.get(0).getId()).orElseThrow(
                DomainExceptionCode.TECH_BLOG_NOT_FOUND::newInstance);

        RedisPostDynamicData redisPostDynamicData = redisPostDynamicDataRepository.findById(posts.get(0).getId())
                .orElseThrow(DomainExceptionCode.REDIS_POST_DYNAMIC_DATA_NOT_FOUND::newInstance);

        assertAll(
                () -> assertThat(techBlogPost.getPostLike()).isEqualTo(300),
                () -> assertThat(redisPostDynamicData.getLikeCount()).isEqualTo(300)
        );
    }


    @Test
    void 여러_사용자가_게시물에_좋아요를_누르면_정상적으로_감소합니다() throws InterruptedException {
        //when
        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        IntStream.rangeClosed(101, 200).forEach(i -> {
            String accessToken = jwtProvider.createAccessToken((long) i, Instant.now());
            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .delete("/api/v1/posts/{postId}/like", posts.get(0).getId());
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();

        });
        latch.await();
        executorService.shutdown();

        //then
        List<TechBlogPost> techBlogPost = techBlogPostRepository.findAll();
        Long postId = techBlogPost.get(0).getId();

        RedisPostDynamicData redisPostDynamicData = redisPostDynamicDataRepository.findById(postId)
                .orElseThrow(DomainExceptionCode.REDIS_POST_DYNAMIC_DATA_NOT_FOUND::newInstance);

        assertAll(
                () -> assertThat(techBlogPost.get(0).getPostLike()).isEqualTo(100),
                () -> assertThat(redisPostDynamicData.getLikeCount()).isEqualTo(100)
        );
    }

    @Test
    void 여러_사용자가_게시물을_읽으면_조회수가_정상적으로_증가합니다() throws InterruptedException {
        //when
        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        IntStream.rangeClosed(1, 100).forEach(i -> {
            String accessToken = jwtProvider.createAccessToken((long) i, Instant.now());
            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .post("/api/v1/members/me/read-post/{postId}", posts.get(0).getId());
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();

        });
        latch.await();
        executorService.shutdown();

        //then
        List<TechBlogPost> techBlogPost = techBlogPostRepository.findAll();
        Long postId = techBlogPost.get(0).getId();

        RedisPostDynamicData redisPostDynamicData = redisPostDynamicDataRepository.findById(postId)
                .orElseThrow(DomainExceptionCode.REDIS_POST_DYNAMIC_DATA_NOT_FOUND::newInstance);

        assertAll(
                () -> assertThat(techBlogPost.get(0).getViewCount()).isEqualTo(200),
                () -> assertThat(redisPostDynamicData.getViewCount()).isEqualTo(200)
        );
    }

}
