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
import com.drrr.domain.log.repository.MemberTechBlogPostRepository;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberViewWeightServiceTest {
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
    private MemberTechBlogPostRepository memberTechBlogPostRepository;

    @Autowired
    private MemberPostHistoryRepository memberPostHistoryRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void teardown(){
        databaseCleaner.clear();
    }


    @BeforeEach
    void setup(){
        databaseCleaner.clear();
        Member member = Member.builder()
                .email("example@drrr.com")
                .nickname("user1")
                .gender(Gender.MAN)
                .provider("kakao")
                .providerId("12345")
                .imageUrl("http://example.com/image")
                .role(MemberRole.USER)
                .build();
        memberRepository.save(member);

        List<TechBlogPost> techBlogPosts = IntStream.rangeClosed(1, 100).mapToObj(i -> {
            //현재로부터 몇년전까지 랜덤으로 연월일을 뽑을지 정함
            LocalDate createdDate = LocalDate.of(2023, 9, 30);
            createdDate.minusDays(i);
            String author = "Author" + i; // 짝수 인덱스에서만 저자 설정
            String thumbnailUrl = "http://example.com/thumbnail" + i + ".jpg";
            String title = "Title" + i;
            String summary = (i % 3 == 0) ? "Summary" + i : null; // 3의 배수 인덱스에서만 요약 설정
            String urlSuffix = "/suffix/" + i;
            String url = "http://example.com/suffix/" + i;
            TechBlogCode techBlogCode = TechBlogCode.values()[i
                    % TechBlogCode.values().length]; // 순환적으로 TechBlogCode 값 할당
            return TechBlogPost.builder()
                    .createdDate(createdDate)
                    .author(author)
                    .thumbnailUrl(thumbnailUrl)
                    .title(title)
                    .summary(summary)
                    .urlSuffix(urlSuffix)
                    .url(url)
                    .crawlerGroup(TechBlogCode.KAKAO)
                    .build();
        }).collect(Collectors.toList());
        techBlogPostRepository.saveAll(techBlogPosts);


        List<Category> categories = IntStream.rangeClosed(1, 10).mapToObj(i -> {
            String categoryName = "Category" + i;
            String categoryDisplayName = "Display Category" + i;
            return Category.builder()
                    .uniqueName(categoryName)
                    .categoryDisplayName(categoryDisplayName)
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

        IntStream.rangeClosed(1, 49).forEach(i -> {
            if (i < 10 && i % 2 == 1) {
                TechBlogPost post = posts.get(i);
                IntStream.rangeClosed(1, 7).forEach(j -> {
                    Category category = categoryList.get(j);
                    techBlogPostCategories.add(TechBlogPostCategory.builder()
                            .post(post)
                            .category(category)
                            .build());
                });
            } else if (i >= 10) {
                TechBlogPost post = posts.get(i);
                Category category = categoryList.get(7);
                techBlogPostCategories.add(TechBlogPostCategory.builder()
                        .post(post)
                        .category(category)
                        .build());
            }
        });

        techBlogPostCategoryRepository.saveAll(techBlogPostCategories);

        /**
         * Member Id 1이 선호하는 카테고리 3, 5, 7
         * Member Id 1이 추가적으로 읽은 카테고리 2, 8
         * Member Id 1의 가중치 값 C2-8, C3-3, C5-4, C7-2, C8-2
         */
        List<Category> categoryWeights = categoryRepository.findByIds(Arrays.asList(2L, 3L, 5L, 7L, 8L)).get();
        List<Double> weights = Arrays.asList(8.0, 3.0, 4.0, 2.0, 2.0);
        List<CategoryWeight> categoryWeightList = new ArrayList<>();
        IntStream.range(0, categoryWeights.size()).forEach(i -> {
            Category category = categoryWeights.get(i);
            double value = weights.get(i);
            boolean preferred = i == 3 || i == 5 || i == 7;

            categoryWeightList.add(new CategoryWeight(member, category, value, preferred));
        });
        categoryWeightRepository.saveAll(categoryWeightList);

    }

    @Test
    void 사용자가_본_게시물의_카테고리에_대한_가중치_증가가_정상적으로_작동합니다(){
        //given - 카테고리 id : 1, 2, 3, 4에 대한 게시물 읽음
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L, 4L);
        Long memberId = memberRepository.findAll().get(0).getId();
        Long postId = techBlogPostRepository.findAll().get(0).getId();

        //when
        memberViewWeightService.increaseMemberViewPost(memberId,postId,categoryIds);

        //then
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findCategoryWeightByMemberId(1L);
        assertThat(categoryWeights).isNotEmpty();

        categoryWeights.forEach(categoryWeight -> assertThat(categoryWeight.getValue())
                .isEqualTo(WeightConstants.INCREASE_WEIGHT.getValue()));

    }

    @Test
    void 사용자가_본_게시물에_대한_로그와_히스토리가_정상적으로_쌓입니다(){
        //given - 카테고리 id : 1, 2, 3, 4에 대한 게시물 읽음
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L, 4L);

        Long memberId = memberRepository.findAll().get(0).getId();
        Long postId = techBlogPostRepository.findAll().get(0).getId();
        //when
        memberViewWeightService.increaseMemberViewPost(memberId,postId,categoryIds);
        TechBlogPost post = techBlogPostRepository.findById(postId).get();

        //then
        List<MemberPostLog> memberLogs = memberTechBlogPostRepository.findByMemberId(memberId).get();
        List<MemberPostHistory> memberHistories = memberPostHistoryRepository.findByMemberId(memberId).get();
        assertThat(memberLogs).isNotEmpty();
        assertThat(memberHistories).isNotEmpty();
        assertThat(post.getViewCount()).isEqualTo(1);

        memberLogs.forEach(log->{
                    assertThat(log.getMemberId()).isEqualTo(memberId);
                    assertThat(log.getPostId()).isEqualTo(postId);
                    LocalDate updatedAt = log.getUpdatedAt().toLocalDate();
                    assertThat(updatedAt).isEqualTo(LocalDate.now());
                    assertThat(log.isRead()).isTrue();
                });
        memberHistories.forEach(history->{
                    assertThat(history.getMemberId()).isEqualTo(memberId);
                    assertThat(history.getPostId()).isEqualTo(postId);
                });
    }

}
