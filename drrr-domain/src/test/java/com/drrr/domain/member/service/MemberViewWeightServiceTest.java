package com.drrr.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.recommandation.constant.WeightConstants;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.category.service.MemberViewWeightService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.fixture.post.TechBlogPostCategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.log.entity.history.MemberPostHistory;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostHistoryRepository;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.ServiceIntegrationTest;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberViewWeightServiceTest extends ServiceIntegrationTest {
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
    private WeightValidationService weightValidationService;
    @Autowired
    private LogUpdateService logUpdateService;
    @Autowired
    private EntityManager em;
    private final int VIEW_COUNT = 0;

    private final double WEIGHT_VALUE = 0;


    @Test
    void 사용자의_선호카테고리_가중치가_최소_선호카테고리_가중치보다_적은_경우_게시물의_카테고리에_대한_가중치_정상적으로_계산됩니다() {
        //given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        TechBlogPost post = TechBlogPostFixture.createTechBlogPost();
        techBlogPostRepository.save(post);

        Category category = CategoryFixture.createCategory();
        categoryRepository.save(category);

        CategoryWeight categoryWeight = CategoryWeightFixture.createCategoryWeight(
                member,
                category,
                WEIGHT_VALUE,
                true
        );
        categoryWeightRepository.save(categoryWeight);

        techBlogPostCategoryRepository.save(TechBlogPostCategoryFixture.createTechBlogPostCategory(post, category));

        em.clear();
        em.flush();

        //when
        weightValidationService.validateWeight(member.getId());
        CategoryWeight weight = categoryWeightRepository.findById(categoryWeight.getId()).orElseThrow(
                DomainExceptionCode.CATEGORY_WEIGHT_NOT_FOUND::newInstance);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue(),
                        weight.getWeightValue()),
                () -> assertThat(weight).isNotNull()
        );
    }

    @Test
    void 사용자가_본_게시물에_대한_로그와_히스토리가_정상적으로_쌓입니다() {
        //given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        TechBlogPost post = TechBlogPostFixture.createTechBlogPost();
        techBlogPostRepository.save(post);

        Category category = CategoryFixture.createCategory();
        categoryRepository.save(category);

        CategoryWeight categoryWeight = CategoryWeightFixture.createCategoryWeight(
                member,
                category,
                WEIGHT_VALUE,
                true
        );
        categoryWeightRepository.save(categoryWeight);

        techBlogPostCategoryRepository.save(TechBlogPostCategoryFixture.createTechBlogPostCategory(post, category));

        em.clear();
        em.flush();

        //when
        logUpdateService.insertMemberLogAndHistory(member.getId(), post.getId());

        //then
        List<MemberPostLog> memberLogs = memberPostLogRepository.findByMemberId(member.getId());
        if (memberLogs.isEmpty()) {
            throw new IllegalArgumentException("memberLog elements is null");
        }
        List<MemberPostHistory> memberHistories = memberPostHistoryRepository.findByMemberId(member.getId());
        if (memberHistories.isEmpty()) {
            throw new IllegalArgumentException("memberHistory elements is null");
        }

        Assertions.assertAll(
                () -> assertThat(memberLogs).isNotEmpty(),
                () -> assertThat(memberHistories).isNotEmpty(),
                () -> memberLogs.forEach(log -> {
                    Assertions.assertAll(
                            () -> assertThat(log.getMemberId()).isEqualTo(member.getId()),
                            () -> assertThat(log.getPostId()).isEqualTo(post.getId()),
                            () -> assertThat(log.getUpdatedAt().toLocalDate()).isEqualTo(LocalDate.now()),
                            () -> assertThat(log.isRead()).isTrue()
                    );
                }),
                () -> memberHistories.forEach(history -> {
                    Assertions.assertAll(
                            () -> assertThat(history.getMemberId()).isEqualTo(member.getId()),
                            () -> assertThat(history.getPostId()).isEqualTo(post.getId()),
                            () -> assertThat(history.getUpdatedAt().toLocalDate()).isEqualTo(LocalDate.now())
                    );
                })
        );
    }

    @Test
    void 사용자가_한_게시물을_접근했을_때_조회수가_정상적으로_증가합니다() {
        //given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        TechBlogPost post = TechBlogPostFixture.createTechBlogPostLike(VIEW_COUNT);
        techBlogPostRepository.save(post);

        Category category = CategoryFixture.createCategory();
        categoryRepository.save(category);

        CategoryWeight categoryWeight = CategoryWeightFixture.createCategoryWeight(
                member,
                category,
                WEIGHT_VALUE,
                true
        );
        categoryWeightRepository.save(categoryWeight);

        techBlogPostCategoryRepository.save(TechBlogPostCategoryFixture.createTechBlogPostCategory(post, category));

        em.clear();
        em.flush();
        //when
        memberViewWeightService.increaseMemberViewPost(member.getId(), post.getId(), List.of(category.getId()));

        //then
        TechBlogPost updatedPost = techBlogPostRepository.findById(post.getId()).orElseThrow(
                DomainExceptionCode.TECH_BLOG_NOT_FOUND::newInstance);

        assertThat(updatedPost.getViewCount()).isEqualTo(1);
    }

}
