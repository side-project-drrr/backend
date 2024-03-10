package com.drrr.domain.techblogpost.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.member.MemberFixture;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.TechBlogPostCategoryFixture;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.DatabaseCleaner;
import com.drrr.domain.util.ServiceIntegrationTest;
import jakarta.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

public class FindPostServiceTest extends ServiceIntegrationTest {
    @Autowired
    DatabaseCleaner databaseCleaner;
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
    private EntityManager em;


    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
        memberRepository.saveAll(MemberFixture.createMembers());
        techBlogPostRepository.saveAll(TechBlogPostFixture.createTechBlogPosts());
        categoryRepository.saveAll(CategoryFixture.createCategories());
        techBlogPostCategoryRepository.saveAll(
                TechBlogPostCategoryFixture.createTechBlogPostCategory(categoryRepository, techBlogPostRepository)
        );
        categoryWeightRepository.saveAll(CategoryWeightFixture.createCategoryWeights(categoryRepository));

        em.clear();
        em.flush();
    }


    @Test
    void 게시글이_슬라이스로_정상적으로_조회됩니다() {
        // when
        Slice<TechBlogPostCategoryDto> allPosts = techBlogPostRepository.findAllPosts(PageRequest.of(0, 10));

        // then
        List<TechBlogPostBasicInfoDto> basicInfoDtos = allPosts.getContent().stream()
                .map(TechBlogPostCategoryDto::techBlogPostBasicInfoDto)
                .toList();
        Comparator<TechBlogPostBasicInfoDto> writtenAtComparator = Comparator.comparing(
                        TechBlogPostBasicInfoDto::writtenAt)
                .reversed();

        assertThat(basicInfoDtos).isSortedAccordingTo(writtenAtComparator);
        assertThat(allPosts.hasNext()).isTrue();
        assertThat(allPosts.isFirst()).isTrue();
        assertThat(allPosts.isLast()).isFalse();
        assertThat(allPosts.getNumber()).isEqualTo(0);
        assertThat(allPosts.getNumberOfElements()).isEqualTo(10);
        assertThat(allPosts.getSize()).isEqualTo(10);
    }

    @Test
    void 카테고리로_게시글_슬라이스_조회가_정상적으로_조회됩니다() {
        // when
        Slice<TechBlogPostCategoryDto> allPosts = techBlogPostRepository.findPostsByCategory(1L, PageRequest.of(0, 10));

        // then
        List<TechBlogPostBasicInfoDto> basicInfoDtos = allPosts.getContent().stream()
                .map(TechBlogPostCategoryDto::techBlogPostBasicInfoDto)
                .toList();
        Comparator<TechBlogPostBasicInfoDto> writtenAtComparator = Comparator.comparing(
                        TechBlogPostBasicInfoDto::writtenAt)
                .reversed();

        assertThat(basicInfoDtos).isSortedAccordingTo(writtenAtComparator);
        assertThat(allPosts.hasNext()).isTrue();
        assertThat(allPosts.isFirst()).isTrue();
        assertThat(allPosts.isLast()).isFalse();
        assertThat(allPosts.getNumber()).isEqualTo(0);
        assertThat(allPosts.getNumberOfElements()).isEqualTo(10);
        assertThat(allPosts.getSize()).isEqualTo(10);
    }
}
