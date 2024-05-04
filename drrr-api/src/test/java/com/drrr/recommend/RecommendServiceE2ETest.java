package com.drrr.recommend;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.recommandation.constant.PostConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.fixture.post.TechBlogPostCategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.member.repository.common.MemberQueryService;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.RedisTemplateTestUtil;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import com.drrr.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RecommendServiceE2ETest {

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
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private RedisTemplateTestUtil redisTemplateTestUtil;
    @Autowired
    private MemberQueryService memberQueryService;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        databaseCleaner.clear();

        //M1~M500 생성
        final List<Member> members = MemberFixture.createMembers(100);
        memberRepository.saveAll(members);

        //P1~P100 생성
        final List<TechBlogPost> techBlogPosts = TechBlogPostFixture.createTechBlogPosts(100);
        techBlogPostRepository.saveAll(techBlogPosts);

        //C1~C10 생성
        final List<Category> categories = CategoryFixture.createCategories(10);
        categoryRepository.saveAll(categories);

        //각 M1~M500의 CW 생성
        final List<CategoryWeight> categoryWeights = CategoryWeightFixture.createCategoryWeights(
                members,
                categories,
                Arrays.asList(8.0, 3.0, 4.0, 10.0, 30.0, 1.0, 23.0, 5.0, 6.0, 7.0),
                Arrays.asList(true, true, true, true, false, true, true, true, true, false)
        );

        categoryWeightRepository.saveAll(categoryWeights);

        final List<TechBlogPost> selectedTechBlogPosts = new ArrayList<>();
        selectedTechBlogPosts.add(techBlogPosts.get(2));
        selectedTechBlogPosts.add(techBlogPosts.get(23));
        selectedTechBlogPosts.add(techBlogPosts.get(32));
        selectedTechBlogPosts.add(techBlogPosts.get(45));
        selectedTechBlogPosts.add(techBlogPosts.get(56));

        final List<TechBlogPost> otherTechBlogPosts = new ArrayList<>(techBlogPosts);
        otherTechBlogPosts.removeAll(selectedTechBlogPosts);

        techBlogPostCategoryRepository.saveAll(
                TechBlogPostCategoryFixture.createTechBlogPostCategories(selectedTechBlogPosts, otherTechBlogPosts,
                        categories));

        redisTemplateTestUtil.flushAll();
    }

    @Test
    void 사용자_게시물_추천_동시성_테스트가_잘_수행됩니다() throws InterruptedException {
        //when
        final CountDownLatch latch = new CountDownLatch(1);
        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final List<List<Long>> membersRecommendedPosts = new ArrayList<>();
        IntStream.rangeClosed(1, 100).forEach(i -> {
            String accessToken = jwtProvider.createAccessToken((long) i, Instant.now());
            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .get("/api/v1/members/me/post-recommendation/{count}",
                            PostConstants.RECOMMEND_POSTS_COUNT.getValue());
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            final List<TechBlogPostResponse> responseBody = response.jsonPath().getList("", TechBlogPostResponse.class);
            membersRecommendedPosts.add(
                    responseBody.stream().map(data -> data.techBlogPostBasicInfoDto().id()).toList()
            );
            latch.countDown();

        });
        latch.await();
        executorService.shutdown();

        //then
        final List<Member> members = memberQueryService.getAllMembers();

        final List<Long> memberIds = members.stream()
                .map(Member::getId).toList();

        final List<RedisSlicePostsContents> cacheMemberRecommendation = redisTemplateTestUtil.findCacheMemberRecommendation(
                memberIds.get(0));

        final List<Long> postIds = cacheMemberRecommendation.stream()
                .map(data -> data.redisTechBlogPostStaticData().id())
                .toList();

        IntStream.range(0, 100).forEach(i -> {
            final List<Long> postsId = membersRecommendedPosts.get(i);
            Assertions.assertAll(
                    () -> assertThat(postsId).containsExactlyInAnyOrder(3L, 24L, 33L, 46L, 57L),
                    () -> assertThat(postsId).containsExactlyInAnyOrder(postIds.toArray(new Long[0]))
            );
        });

    }

    @Test
    void 사용자가_추천게시물_중_하나를_읽으면_다른_게시물을_정상적으로_추천합니다() {
        //when
        final String accessToken = jwtProvider.createAccessToken(1L, Instant.now());
        final Response oldsRecommendationResponse = given().log()
                .all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .get("/api/v1/members/me/post-recommendation/{count}",
                        PostConstants.RECOMMEND_POSTS_COUNT.getValue());
        oldsRecommendationResponse.then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        final List<TechBlogPostResponse> oldResponseBody = oldsRecommendationResponse.jsonPath()
                .getList("", TechBlogPostResponse.class);
        final List<Long> oldPostIds = oldResponseBody.stream().map(data -> data.techBlogPostBasicInfoDto().id()).toList();

        final Long memberReadPostId = oldPostIds.get(0);

        final Response memberReadResponse = given().log()
                .all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .post("/api/v1/members/me/read-post/{postId}", memberReadPostId);
        memberReadResponse.then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        final Response newRecommendationResponse = given().log()
                .all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .get("/api/v1/members/me/post-recommendation/{count}",
                        PostConstants.RECOMMEND_POSTS_COUNT.getValue());
        newRecommendationResponse.then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        final List<TechBlogPostResponse> newResponseBody = newRecommendationResponse.jsonPath()
                .getList("", TechBlogPostResponse.class);

        final List<Long> newPostIds = newResponseBody.stream().map(data -> data.techBlogPostBasicInfoDto().id()).toList();

        final List<RedisSlicePostsContents> cacheMemberRecommendation = redisTemplateTestUtil.findCacheMemberRecommendation(
                1L);

        final List<Long> cachedPostIds = cacheMemberRecommendation.stream()
                .map(data -> data.redisTechBlogPostStaticData().id())
                .toList();

        //then
        Assertions.assertAll(
                () -> assertThat(newPostIds).contains(1L, 24L, 33L, 46L, 57L),
                () -> assertThat(cachedPostIds).contains(1L, 24L, 33L, 46L, 57L)
        );
    }
}
