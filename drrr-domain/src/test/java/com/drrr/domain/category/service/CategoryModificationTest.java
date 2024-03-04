package com.drrr.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.core.recommandation.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.DatabaseCleaner;
import com.drrr.domain.util.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <h3>Given</h3>
 * <br>Member Id M1(Id 1) 생성</br>
 * <br>M1의 초기 선호 카테고리 C1, C2, C3</br>
 * <br>M1의 변경 선호 카테고리 C6, C7, C8</br>
 * <br>Post Id P1 생성</br>
 * <br>Category Id C1~C10 생성</br>
 * <br>P1-C1, C3, C5에 속함</br>
 */
public class CategoryModificationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private CategoryWeightRepository categoryWeightRepository;
    @Autowired
    private MemberPreferredCategoryServiceModificationService modificationService;

    @BeforeEach
    void setup() {
        databaseCleaner.clear();
        Member member = Member.builder()
                .email("example+@drrr.com")
                .nickname("user")
                .provider("kakao")
                .providerId("12345")
                .build();
        memberRepository.save(member);

        LocalDate createdDate = LocalDate.of(2023, 9, 30);
        String author = "Author1";
        String thumbnailUrl = "http://example.com/thumbnail1.jpg";
        String title = "Title";
        String summary = "Summary";
        String urlSuffix = "/suffix/";
        String url = "http://example.com/suffix/";
        TechBlogCode techBlogCode = TechBlogCode.values()[0]; // 순환적으로 TechBlogCode 값 할당
        TechBlogPost post = TechBlogPost.builder()
                .writtenAt(createdDate)
                .author(author)
                .thumbnailUrl(thumbnailUrl)
                .title(title)
                .summary(summary)
                .aiSummary(summary)
                .urlSuffix(urlSuffix)
                .url(url)
                .crawlerGroup(TechBlogCode.KAKAO)
                .build();
        techBlogPostRepository.save(post);

        List<Category> categories = IntStream.rangeClosed(1, 10).mapToObj(i -> {
            String categoryName = "Category" + i;

            return Category.builder()
                    .name(categoryName)
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

        List<CategoryWeight> categoryWeights = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> {
            categoryWeights.add(CategoryWeight.builder()
                    .member(member)
                    .category(categories.get(i))
                    .lastReadAt(LocalDateTime.now().minusDays(3))
                    .preferred(true)
                    .weightValue(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue())
                    .build());
        });

        categoryWeightRepository.saveAll(categoryWeights);
    }

    @Test
    void 사용자_선호_카테리가_제대로_변경됩니다() {
        //when
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new RuntimeException(
                    "Cannot find such categories");
        }

        List<Long> updateCategoryIds = new ArrayList<>();
        IntStream.range(5, 8).forEach(i -> {
            updateCategoryIds.add(categories.get(i).getId());
        });

        //카테고리 업데이트
        modificationService.changeMemberPreferredCategory(1L, updateCategoryIds);
        List<Long> newPreferredCategoryIds = Arrays.asList(6L, 7L, 8L);
        List<Long> oldPreferredCategoryIds = Arrays.asList(1L, 2L, 3L);
        //then
        List<CategoryWeight> memberCategoryWeights = categoryWeightRepository.findByMemberId(1L);
        List<Long> categoryIds = memberCategoryWeights.stream()
                .map(category -> {
                    return category.getCategory().getId();
                }).toList();

        //선호 카테고리가 추가됐는지 확인
        assertThat(categoryIds).containsAnyOf(6L, 7L, 8L);
        //총 6개의 카테고리가 있는지 확인(선호 카테고리 3, 비선호 카테고리 3)
        assertThat(categoryIds).size().isEqualTo(6);
        //6, 7, 8 카테고리 id에 대해서 선호가 true인지 확인
        assertThat(memberCategoryWeights)
                .filteredOn(categoryWeight -> newPreferredCategoryIds.contains(categoryWeight.getCategory().getId()))
                .extracting(CategoryWeight::isPreferred)
                .containsOnly(true);
        //1, 2, 3 카테고리 id에 대해서 선호가 false인지 확인
        assertThat(memberCategoryWeights)
                .filteredOn(categoryWeight -> oldPreferredCategoryIds.contains(categoryWeight.getCategory().getId()))
                .extracting(CategoryWeight::isPreferred)
                .containsOnly(false);
    }
}
