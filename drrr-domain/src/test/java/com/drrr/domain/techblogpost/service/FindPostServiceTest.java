package com.drrr.domain.techblogpost.service;


import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.fixture.category.CategoryFixture;
import com.drrr.domain.fixture.category.weight.CategoryWeightFixture;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.fixture.post.TechBlogPostCategoryFixture;
import com.drrr.domain.fixture.post.TechBlogPostFixture;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostSliceDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostStaticDataDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.ServiceIntegrationTest;
import jakarta.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FindPostServiceTest extends ServiceIntegrationTest {
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
    final static int POST_COUNTS = 20;
    final static int CATEGORY_COUNT = 10;


    @Test
    void 게시글이_슬라이스로_정상적으로_조회됩니다() {
        //given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        List<TechBlogPost> posts = TechBlogPostFixture.createTechBlogPosts(POST_COUNTS);
        List<Category> categories = CategoryFixture.createCategories(CATEGORY_COUNT);

        techBlogPostRepository.saveAll(TechBlogPostFixture.createTechBlogPosts(POST_COUNTS));
        categoryRepository.saveAll(CategoryFixture.createCategories(CATEGORY_COUNT));

        techBlogPostCategoryRepository.saveAll(
                TechBlogPostCategoryFixture.createTechBlogPostCategories(posts, categories)
        );

        categoryWeightRepository.saveAll(CategoryWeightFixture.createCategoryWeights(member, categories));

        em.clear();
        em.flush();

        // when
        TechBlogPostSliceDto allPosts = techBlogPostRepository.findAllPosts(PageRequest.of(0, 10));

        // then
        List<TechBlogPostStaticDataDto> staticDataDtos = allPosts.contents().stream()
                .map(TechBlogPostCategoryDto::techBlogPostStaticDataDto)
                .toList();
        Comparator<TechBlogPostStaticDataDto> writtenAtComparator = Comparator.comparing(
                        TechBlogPostStaticDataDto::writtenAt)
                .reversed();

        Assertions.assertAll(
                () -> Assertions.assertTrue(allPosts.hasNext()),
                () -> Assertions.assertEquals(10, allPosts.contents().size()),
                () -> Assertions.assertEquals(staticDataDtos.stream().sorted(writtenAtComparator).toList(),
                        staticDataDtos)
        );

    }

    @Test
    void 카테고리로_게시글_슬라이스_조회가_정상적으로_조회됩니다() {
        //given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        List<TechBlogPost> posts = TechBlogPostFixture.createTechBlogPosts(POST_COUNTS);
        techBlogPostRepository.saveAll(posts);

        Category category = CategoryFixture.createCategory();
        categoryRepository.save(category);

        List<TechBlogPostCategory> techBlogPostCategory = TechBlogPostCategoryFixture.createTechBlogPostCategories(
                posts,
                category);
        techBlogPostCategoryRepository.saveAll(
                techBlogPostCategory
        );

        categoryWeightRepository.save(CategoryWeightFixture.createCategoryWeight(member, category));

        em.clear();
        em.flush();

        // when
        TechBlogPostSliceDto postsByCategory = techBlogPostRepository.findPostsByCategory(
                category.getId(),
                PageRequest.of(0, 10)
        );

        // then
        List<TechBlogPostStaticDataDto> staticDataDtos = postsByCategory.contents().stream()
                .map(TechBlogPostCategoryDto::techBlogPostStaticDataDto)
                .toList();
        Comparator<TechBlogPostStaticDataDto> writtenAtComparator = Comparator.comparing(
                        TechBlogPostStaticDataDto::writtenAt)
                .reversed();

        Assertions.assertAll(
                () -> Assertions.assertTrue(postsByCategory.hasNext()),
                () -> Assertions.assertEquals(10, postsByCategory.contents().size()),
                () -> Assertions.assertEquals(staticDataDtos.stream().sorted(writtenAtComparator).toList(),
                        staticDataDtos));
    }
}
