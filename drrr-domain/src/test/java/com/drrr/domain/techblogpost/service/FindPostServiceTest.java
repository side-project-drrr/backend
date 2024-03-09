package com.drrr.domain.techblogpost.service;


import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.member.MemberFixture;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
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
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
        memberRepository.saveAll(MemberFixture.createMembers());
        techBlogPostRepository.saveAll(TechBlogPostFixture.createTechBlogPosts());
        categoryRepository.saveAll(CategoryFixture.createCategories());
        categoryWeightRepository.saveAll(CategoryWeightFixture.createCategoryWeights());

    }

    @Test
    void 게시글_조회_기능_테스트() {
        techBlogPostRepository.findAllPosts(PageRequest.of(0, 10));

        // when
        Slice<TechBlogPostCategoryDto> allPosts = techBlogPostRepository.findAllPosts(PageRequest.of(0, 10));

        // then
        List<TechBlogPostBasicInfoDto> basicInfoDtos = allPosts.getContent().stream()
                .map(TechBlogPostCategoryDto::techBlogPostBasicInfoDto)
                .toList();
        assertThat(allPosts.getContent()).hasSize(10);

        Comparator<TechBlogPostBasicInfoDto> writtenAtComparator = Comparator.comparing(
                        TechBlogPostBasicInfoDto::writtenAt)
                .reversed();

        assertThat(basicInfoDtos).isSortedAccordingTo(writtenAtComparator);
    }
}
