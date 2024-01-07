package com.drrr.member;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.member.Gender;
import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.entity.MemberRole;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
import io.restassured.RestAssured;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
 * <h2>같은 post에 대한 100 Members의 동시 접근 테스트</h2>
 * <br>Post Id 1에 대해서 100명의 사용자가 동시 접근했을 때 조회수가 제대로 증가되어 조회수가 100이 되는지 확인</br>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MemberViewE2ETest {
    private final int CATEGORY_COUNT = 20;
    private final int MEMBER_COUNT = 100;
    private final int POST_COUNT = 1;
    private final int CATEGORY_WEIGHT_COUNT = 100;
    private final int MEMBER_POST_LOG_COUNT = 100;
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
        insertMemberDummyData();
        insertPostDummyData();
        insertCategoryDummyData();
        insertCategoryWeightDummyData();
        insertMemberPostLogDummyData();
        insertPostCategoryDummyData();
    }


    private void insertMemberDummyData() {
        List<Member> members = IntStream.rangeClosed(1, MEMBER_COUNT).mapToObj(i -> {
            String email = "user" + i + "@example.com";
            String nickname = "user" + i;
            Gender gender = (i % 2 == 0) ? Gender.MAN : Gender.WOMAN; // 임의로 남성과 여성을 번갈아가며 설정
            String provider = "provider" + i;
            String providerId = "providerId" + i;
            String imageUrl = "http://example.com/image" + i + ".jpg";
            MemberRole role = MemberRole.USER; // 임의로 USER와 ADMIN을 번갈아가며 설정
            return Member.createMember(email, nickname, provider, providerId, imageUrl);
        }).collect(Collectors.toList());
        memberRepository.saveAll(members);
    }

    private void insertPostDummyData() {
        List<TechBlogPost> techBlogPosts = IntStream.rangeClosed(1, POST_COUNT).mapToObj(i -> {
            //현재로부터 몇년전까지 랜덤으로 연월일을 뽑을지 정함
            int yearsBeforeRange = 2018;
            LocalDate createdDate = RandomLocalDate(yearsBeforeRange);
            String author = "Author" + i; // 짝수 인덱스에서만 저자 설정
            String thumbnailUrl = "http://example.com/thumbnail" + i + ".jpg";
            String title = "Title" + i;
            String summary = (i % 3 == 0) ? "Summary" + i : null; // 3의 배수 인덱스에서만 요약 설정
            String urlSuffix = "/suffix/" + i;
            String url = "http://example.com/suffix/" + i;
            int viewCount = 0;
            TechBlogCode techBlogCode = TechBlogCode.values()[i
                    % TechBlogCode.values().length]; // 순환적으로 TechBlogCode 값 할당
            return new TechBlogPost(createdDate, author, thumbnailUrl, title, summary, summary, urlSuffix, url,
                    techBlogCode, viewCount, viewCount);
        }).collect(Collectors.toList());
        techBlogPostRepository.saveAll(techBlogPosts);
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

    private void insertCategoryWeightDummyData() {
        List<Member> members = memberRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<CategoryWeight> categoryWeights = new ArrayList<>();

        IntStream.range(0, CATEGORY_WEIGHT_COUNT).forEach(i -> {
            List<Integer> randomCategoryList = getRangeShuffledList(1, CATEGORY_COUNT);
            int preferCategoryCnt = getRandomValueInRange(Integer.class, 1, 8);
            Member member = members.get(i); // 순환적으로 Member 객체 할당
            IntStream.rangeClosed(1, preferCategoryCnt).forEach(j -> {
                Category category = categories.get(randomCategoryList.get(j));
                double value = getRandomValueInRange(Double.class, 0.0, 1.0); // 가중치 값 설정 (여기서는 단순히 인덱스에 0.01을 곱한 값을 사용)
                boolean preferred = true; // 짝수 인덱스에서만 선호 카테고리로 설정

                categoryWeights.add(CategoryWeight.builder()
                        .member(member)
                        .category(category)
                        .weightValue(value)
                        .preferred(preferred)
                        .lastReadAt(LocalDateTime.now())
                        .build());
            });
        });

        categoryWeightRepository.saveAll(categoryWeights);
    }

    private void insertMemberPostLogDummyData() {
        List<MemberPostLog> logs = new ArrayList<>();
        IntStream.range(0, MEMBER_POST_LOG_COUNT).forEach(i -> {
            List<Member> randomMemberIds = memberRepository.findAll();
            List<TechBlogPost> randomPostIds = techBlogPostRepository.findAll();
            Collections.shuffle(randomPostIds);
            Long randomMemberId = randomMemberIds.get(i).getId(); // 임의로 회원 ID 할당
            //멤버마다 몇개의 로그를 만들지 정함
            int createEachMemberLogCount = 1;
            IntStream.range(0, createEachMemberLogCount).forEach(j -> {
                int randomBoolean1 = getRandomValueInRange(Integer.class, 1, 3);
                int randomBoolean2 = getRandomValueInRange(Integer.class, 1, 3);
                long randomPostId = 1L;
                boolean isRead = (randomBoolean1 == 1); // 짝수 인덱스에서만 읽음으로 설정
                boolean isRecommended = (randomBoolean2 == 1); // 3의 배수 인덱스에서만 추천으로 설정

                logs.add(MemberPostLog.builder()
                        .memberId(randomMemberId)
                        .postId(randomPostId)
                        .isRead(isRead)
                        .lastReadAt(LocalDateTime.now())
                        .isRecommended(isRecommended)
                        .build());
            });
        });
        memberPostLogRepository.saveAll(logs);
    }


    private void insertPostCategoryDummyData() {

        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<TechBlogPostCategory> techBlogPostCategories1 = new ArrayList<>();
        Collections.shuffle(posts);
        IntStream.range(0, POST_COUNT).forEach(i -> {
            Collections.shuffle(categories);
            TechBlogPost post = posts.get(i);
            int randomCategoryCount = getRandomValueInRange(Integer.class, 1, MAX_PREFER_CATEGORIES_COUNT);
            IntStream.rangeClosed(0, randomCategoryCount).forEach(j -> {
                Category category = categories.get(j);
                techBlogPostCategories1.add(TechBlogPostCategory.builder()
                        .post(post)
                        .category(category)
                        .build());
            });
        });

        techBlogPostCategoryRepository.saveAll(techBlogPostCategories1);
    }


    LocalDate RandomLocalDate(int yearFrom) {
        Random random = new Random();
        LocalDate now = LocalDate.now();
        LocalDate startYear = LocalDate.of(yearFrom, 1, 1);

        long daysBetween = ChronoUnit.DAYS.between(startYear, now);
        long randomDaysToSubtract = (long) (random.nextDouble() * daysBetween);

        return now.minusDays(randomDaysToSubtract);
    }

    public <T> T getRandomValueInRange(Class<T> wrapperClass, double startRange, double endRange) {
        Random random = new Random();
        double randomValue = startRange + (endRange - startRange) * random.nextDouble();

        if (wrapperClass.equals(Integer.class)) {
            return wrapperClass.cast((int) randomValue);
        } else if (wrapperClass.equals(Long.class)) {
            return wrapperClass.cast((long) randomValue);
        } else if (wrapperClass.equals(Double.class)) {
            return wrapperClass.cast(randomValue);
        } else if (wrapperClass.equals(Float.class)) {
            return wrapperClass.cast((float) randomValue);
        } else {
            throw new IllegalArgumentException("Unsupported wrapper class: " + wrapperClass.getName());
        }
    }

    public List<Integer> getRangeShuffledList(int start, int end) {
        List<Integer> shuffleList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            shuffleList.add(i);
        }
        Collections.shuffle(shuffleList);
        return shuffleList;
    }

    @Test
    void 여러_사용자가_한_게시물을_접근했을_때_조회수가_정상적으로_증가합니다() throws InterruptedException {
        //when
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            throw new IllegalArgumentException("member elements is null");
        }
        List<TechBlogPost> originalPost = techBlogPostRepository.findAll();
        if (originalPost.isEmpty()) {
            throw new IllegalArgumentException("TechBlogPost elements is null");
        }
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L, 4L);

        CountDownLatch latch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        IntStream.rangeClosed(1, 100).forEach(i -> {
            String accessToken = jwtProvider.createAccessToken(Long.valueOf(i), Instant.now());
            given().log()
                    .all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .contentType(ContentType.APPLICATION_JSON.toString())
                    .body("""
                            {
                                "categoryIds": [1,11]
                            }
                            """)
                    .post("/api/v1/posts/read/{postId}", 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all();

            latch.countDown();
        });

        latch.await();
        executorService.shutdown();

        //then
        List<TechBlogPost> updatedPost = techBlogPostRepository.findAll();
        if (updatedPost.isEmpty()) {
            throw new IllegalArgumentException("TechBlogPost elements is null");
        }
        int viewCount = updatedPost.get(0).getViewCount();
        assertThat(viewCount).isEqualTo(100);
    }
}
