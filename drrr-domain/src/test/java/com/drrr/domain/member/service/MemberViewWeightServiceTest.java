package com.drrr.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.Gender;
import com.drrr.core.code.TechBlogCode;
import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.category.service.MemberViewWeightService;
import com.drrr.domain.log.entity.history.MemberPostHistory;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostHistoryRepository;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.entity.MemberRole;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.DatabaseCleaner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <hr>이를 @SpringBootTest를 한 이유<hr>
 * <br>@DataJpaTest는 비동기 작업을 수행하는 경우 특정 상황에서 문제가 발생할 수 있다. 이는 기본적으로</br>
 * <br>테스트 메서드가 종료된 후에 트랜잭션을 롤백하는데 별도의 쓰레드에서 실행되는 작업은 이 트랜잭션과 분리되어 있으므로</br>
 * <br>해당 작업이 완료되기 전에 롤백이 발생할 수 있음. @DataJpaTest와 같은 단위 테스트 환경에서 비동기 로직을 사용하는 것은 일반적으로 권장하지 않음</br>
 */
@SpringBootTest
class MemberViewWeightServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryWeightRepository categoryWeightRepository;

    @Autowired
    private MemberViewWeightService memberViewWeightService;

    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;

    @Autowired
    private MemberPostLogRepository memberPostLogRepository;

    @Autowired
    private MemberPostHistoryRepository memberPostHistoryRepository;
    @Autowired
    private LogUpdateService logUpdateService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void teardown() {
        databaseCleaner.clear();
    }


    /**
     * <h3>Given</h3>
     * <br>Member Id M1(Id 1)~M500 생성</br>
     * <br>Post Id P1 생성</br>
     * <br>Category Id C1~C10 생성</br>
     * <br>P1-C1, C3, C5에 속함
     *
     * <h2>M1~M50이 P1를 읽음</h2>
     * <br>log와 history가 쌓이고 isRead 상태가 true로 변환되고 읽은 날짜 검증</br>
     */
    @BeforeEach
    void setup() {
        databaseCleaner.clear();
        IntStream.rangeClosed(0, 500).forEach(i -> {
            Member member = Member.builder()
                    .email("example" + i + "+@drrr.com")
                    .nickname("user" + i)
                    .gender(Gender.MAN)
                    .provider("kakao")
                    .providerId("12345" + i)
                    .imageUrl("http://example.com/image" + i)
                    .role(MemberRole.USER)
                    .build();
            memberRepository.save(member);
        });

        LocalDate createdDate = LocalDate.of(2023, 9, 30);
        createdDate.minusDays(1);
        String author = "Author1";
        String thumbnailUrl = "http://example.com/thumbnail1.jpg";
        String title = "Title";
        String summary = "Summary";
        String urlSuffix = "/suffix/";
        String url = "http://example.com/suffix/";
        TechBlogCode techBlogCode = TechBlogCode.values()[0]; // 순환적으로 TechBlogCode 값 할당
        TechBlogPost post = TechBlogPost.builder()
                .createdDate(createdDate)
                .author(author)
                .thumbnailUrl(thumbnailUrl)
                .title(title)
                .summary(summary)
                .urlSuffix(urlSuffix)
                .url(url)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build();
        techBlogPostRepository.save(post);

        List<Category> categories = IntStream.rangeClosed(1, 10).mapToObj(i -> {
            String categoryName = "Category" + i;
            String categoryDisplayName = "Display Category" + i;
            return Category.builder()
                    .uniqueName(categoryName)
                    .displayName(categoryDisplayName)
                    .build();
        }).collect(Collectors.toList());
        categoryRepository.saveAll(categories);
        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        List<Category> categoryList = categoryRepository.findAll();
        List<TechBlogPostCategory> techBlogPostCategories = new ArrayList<>();

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

        techBlogPostCategoryRepository.saveAll(techBlogPostCategories);

    }

    @Test
    void 사용자가_본_게시물의_카테고리에_대한_가중치_증가가_정상적으로_작동합니다() {
        //when
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L, 4L);
        Long memberId = memberRepository.findAll().get(0).getId();
        Long postId = techBlogPostRepository.findAll().get(0).getId();
        memberViewWeightService.increaseMemberViewPost(memberId, postId, categoryIds);

        //then
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findCategoryWeightByMemberId(1L);
        assertThat(categoryWeights).isNotEmpty();

        categoryWeights.forEach(categoryWeight -> assertThat(categoryWeight.getValue())
                .isEqualTo(WeightConstants.INCREASE_WEIGHT.getValue()));

    }

    @Test
    void 사용자가_본_게시물에_대한_로그와_히스토리가_정상적으로_쌓입니다() {
        //when
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L, 4L);

        Long memberId = memberRepository.findAll().get(0).getId();
        Long postId = techBlogPostRepository.findAll().get(0).getId();

        logUpdateService.insertMemberPostReadLog(memberId, postId);
        logUpdateService.insertMemberPostHistory(memberId, postId);

        //then
        List<MemberPostLog> memberLogs = memberPostLogRepository.findByMemberId(memberId);
        if (memberLogs.size() == 0) {
            throw new IllegalArgumentException("memberLog elements is null");
        }
        List<MemberPostHistory> memberHistories = memberPostHistoryRepository.findByMemberId(memberId);
        if (memberHistories.size() == 0) {
            throw new IllegalArgumentException("memberHistory elements is null");
        }
        assertThat(memberLogs).isNotEmpty();
        assertThat(memberHistories).isNotEmpty();

        memberLogs.forEach(log -> {
            assertThat(log.getMemberId()).isEqualTo(memberId);
            assertThat(log.getPostId()).isEqualTo(postId);
            LocalDate updatedAt = log.getUpdatedAt().toLocalDate();
            assertThat(updatedAt).isEqualTo(LocalDate.now());
            assertThat(log.isRead()).isTrue();
        });
        memberHistories.forEach(history -> {
            assertThat(history.getMemberId()).isEqualTo(memberId);
            assertThat(history.getPostId()).isEqualTo(postId);
            LocalDate updatedAt = history.getUpdatedAt().toLocalDate();
            assertThat(updatedAt).isEqualTo(LocalDate.now());
        });
    }

    @Test
    void 여러_사용자가_한_게시물을_접근했을_때_조회수가_정상적으로_증가합니다() throws InterruptedException {
        //when
        List<Member> members = memberRepository.findAll();
        if (members.size() == 0) {
            throw new IllegalArgumentException("member elements is null");
        }
        List<TechBlogPost> originalPost = techBlogPostRepository.findAll();
        if (originalPost.size() == 0) {
            throw new IllegalArgumentException("TechBlogPost elements is null");
        }
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L, 4L);

        CountDownLatch latch = new CountDownLatch(50);
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        IntStream.range(0,500).forEach(i->{
            int idx = i;
            executorService.submit(() -> {
                memberViewWeightService.increaseMemberViewPost(members.get(idx).getId(), originalPost.get(0).getId(),
                        categoryIds);
                latch.countDown();
            });
        });

        latch.await();
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        //then

        List<TechBlogPost> updatedPost = techBlogPostRepository.findAll();
        if (updatedPost.size() == 0) {
            throw new IllegalArgumentException("TechBlogPost elements is null");
        }
        int viewCount = updatedPost.get(0).getViewCount();
        assertThat(viewCount).isEqualTo(500);
    }

}
