package com.drrr.push;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.infra.push.entity.PushStatus;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import com.drrr.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
public class PushE2ETest {
    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PushStatusRepository pushStatusRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private CategoryWeightRepository categoryWeightRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DatabaseCleaner databaseCleaner;
    private final static int MEMBER_COUNT = 1;
    private final static int CATEGORY_COUNT = 3;
    private final static int POST_COUNT = 3;
    private final static int POST_CATEGORY_COUNT = 3;
    private final static int CATEGORY_WEIGHT_COUNT = 3;

    @BeforeEach
    private void setup() {
        RestAssured.port = port;
        databaseCleaner.clear();

        //M1 생성
        String email = "user@example.com";
        String nickname = "user";
        String provider = "provider";
        String providerId = "providerId";
        String imageUrl = "http://example.com/image.jpg";
        Member member = Member.createMember(email, nickname, provider, providerId, imageUrl);
        memberRepository.save(member);

        List<Category> categories = IntStream.rangeClosed(1, CATEGORY_COUNT).mapToObj(i -> {
            String categoryDisplayName = "Display Category" + i;
            return Category.builder()
                    .name(categoryDisplayName)
                    .build();
        }).collect(Collectors.toList());

        categoryRepository.saveAll(categories);

        //P1~P100 생성
        List<TechBlogPost> techBlogPosts = IntStream.rangeClosed(1, POST_COUNT).mapToObj(i -> {
            //현재로부터 몇년전까지 랜덤으로 연월일을 뽑을지 정함
            LocalDate createdDate = LocalDate.now();
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
        techBlogPostRepository.saveAll(techBlogPosts);

        List<TechBlogPostCategory> techBlogPostCategories = new ArrayList<>();

        IntStream.range(0, POST_CATEGORY_COUNT).forEach(i -> {
            Category category = categories.get(i);
            techBlogPostCategories.add(TechBlogPostCategory.builder()
                    .post(techBlogPosts.get(i))
                    .category(category)
                    .build());
        });
        techBlogPostCategoryRepository.saveAll(techBlogPostCategories);

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

        pushStatusRepository.save(PushStatus.builder()
                .memberId(1L)
                .pushDate(LocalDate.now())
                .postIds(techBlogPosts.stream().map(TechBlogPost::getId).collect(Collectors.toList()))
                .build());
    }

    @Test
    void 사용자의_푸시_게시물을_잘_가져옵니다() throws JsonProcessingException {
        //given
        String accessToken = jwtProvider.createAccessToken(1L, LocalDateTime.now().toInstant(ZoneOffset.UTC));

        //when

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        List<TechBlogPostCategoryDto> postCategoryDtos =
                objectMapper.readValue(
                        given()
                                .log().all()
                                .param("memberId", 1L)
                                .param("pushDate", LocalDate.now().toString())
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .header("Authorization", "Bearer " + accessToken)
                                .get("/api/v1/push/posts/member")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(),
                        new TypeReference<List<TechBlogPostCategoryDto>>() {
                        }
                );

        //then
        List<Long> postIds = postCategoryDtos.stream()
                .map((postCategory) -> {
                    return postCategory.techBlogPostBasicInfoDto().id();
                }).toList();

        assertThat(postCategoryDtos.size()).isEqualTo(POST_COUNT);
        assertThat(postIds).containsExactly(1L, 2L, 3L);
    }

    @Test
    void 사용자_구독신청이_잘_작동합니다() {
        //given
        String accessToken = jwtProvider.createAccessToken(1L, LocalDateTime.now().toInstant(ZoneOffset.UTC));

        //when
        given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + accessToken)
                .body("""
                               {
                                   "endpoint": "test-endpoint",
                                   "p256dh" : "test-p256dh",
                                   "auth" : "test-auth",
                                   "memberId" : 1
                               }
                        """)
                .post("/api/v1/member/subscription")
                .then()
                .statusCode(HttpStatus.OK.value());

        //then
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findByMemberId(1L);
        Subscription subscription = subscriptionOptional.get();

        assertThat(subscriptionOptional).isPresent();
        assertThat(subscription).isNotNull();
        assertThat(subscription.getMemberId()).isEqualTo(1);
    }

    @Test
    void 사용자_구독취소가_잘_작동합니다() {
        //given
        String accessToken = jwtProvider.createAccessToken(1L, LocalDateTime.now().toInstant(ZoneOffset.UTC));

        //when
        given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + accessToken)
                .delete("/api/v1/member/subscription")
                .then()
                .statusCode(HttpStatus.OK.value());

        //then
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findByMemberId(1L);

        assertThat(subscriptionOptional).isNotPresent();
    }
}
