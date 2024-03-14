package com.drrr.techblogpost;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.fixture.post.TechBlogPostCategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
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
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "spring.jpa.show-sql=true")
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
    private DatabaseCleaner databaseCleaner;
    final static int MEMBER_COUNT = 100;
    final static int ZERO_LIKE = 0;
    final static int HUNDRED_LIKES = 100;

    /*    @Transactional*/
    @BeforeEach
    void setup() {
        RestAssured.port = port;
        databaseCleaner.clear();
    }


    @Test
    void 여러_사용자가_게시물에_좋아요를_누르면_정상적으로_증가합니다() throws InterruptedException {
        //given
        List<Member> members = MemberFixture.createMembers(MEMBER_COUNT);
        List<Member> memberEntities = memberRepository.saveAll(members);

        TechBlogPost postEntity = techBlogPostRepository.save(
                TechBlogPostFixture.createTechBlogPostLike(ZERO_LIKE));

        Category categoryEntities = categoryRepository.save(
                CategoryFixture.createCategory());

        categoryWeightRepository.saveAll(
                CategoryWeightFixture.createCategoryWeights(memberEntities, categoryEntities));

        techBlogPostCategoryRepository.save(
                TechBlogPostCategoryFixture.createTechBlogPostCategory(
                        postEntity,
                        categoryEntities));

        //when
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        memberEntities.stream().forEach(member -> {
            String accessToken = jwtProvider.createAccessToken(member.getId(), Instant.now());
            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .post("/api/v1/post/{postId}/like", postEntity.getId());
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();

        });
        latch.await();
        executorService.shutdown();

        //then
        TechBlogPost techBlogPost = techBlogPostRepository.findById(postEntity.getId()).orElseThrow(
                DomainExceptionCode.TECH_BLOG_NOT_FOUND::newInstance);

        int likeCount = techBlogPost.getPostLike();

        assertThat(likeCount).isEqualTo(100);

    }

    @Test
    void 여러_사용자가_게시물에_좋아요를_누르면_정상적으로_감소합니다() throws InterruptedException {
        //given
        List<Member> members = MemberFixture.createMembers(MEMBER_COUNT);
        List<Member> memberEntities = memberRepository.saveAll(members);

        TechBlogPost postEntity = techBlogPostRepository.save(
                TechBlogPostFixture.createTechBlogPostLike(HUNDRED_LIKES));

        Category categoryEntities = categoryRepository.save(
                CategoryFixture.createCategory());

        categoryWeightRepository.saveAll(
                CategoryWeightFixture.createCategoryWeights(memberEntities, categoryEntities));

        techBlogPostCategoryRepository.save(
                TechBlogPostCategoryFixture.createTechBlogPostCategory(
                        postEntity,
                        categoryEntities));

        //when
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        memberEntities.stream().forEach(member -> {
            String accessToken = jwtProvider.createAccessToken(member.getId(), Instant.now());
            Response response = given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .delete("/api/v1/post/{postId}/like", postEntity.getId());
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();

        });
        latch.await();
        executorService.shutdown();

        //then
        TechBlogPost techBlogPost = techBlogPostRepository.findById(postEntity.getId()).orElseThrow(
                DomainExceptionCode.TECH_BLOG_NOT_FOUND::newInstance);

        int likeCount = techBlogPost.getPostLike();
        assertThat(likeCount).isEqualTo(0);
    }

}
