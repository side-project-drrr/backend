package com.drrr.domain.techblogpost.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.drrr.core.code.TechBlogCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.jpa.config.QueryDSLConfiguration;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import com.drrr.domain.util.DatabaseCleaner;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@Import({QueryDSLConfiguration.class, DatabaseCleaner.class})
class RegisterPostTagServiceTest {

    // system under test
    @Autowired
    private RegisterPostTagService sut;

    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TemporalTechPostTagRepository temporalTechPostTagRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    public void teardown() {
        databaseCleaner.clear();
    }

    @Test
    void 임시_게시글의_카테고리가_정상적으로_등록됩니다() {
        // given
        // 임시 기술블로그 데이터 생성
        final TemporalTechBlogPost post = temporalTechBlogPostRepository.save(TemporalTechBlogPost.builder()
                .author("author")
                .createdDate(LocalDate.now())
                .thumbnailUrl(null)
                .title("title")
                .summary("summary")
                .url("")
                .urlSuffix("suffix")
                .techBlogCode(TechBlogCode.BASE)
                .crawledDate(LocalDate.now())
                .build());
        // 카테고리 목록 생성
        final List<Category> categories = categoryRepository.saveAll(IntStream.rangeClosed(1, 3)
                .mapToObj(i -> Category.builder()
                        .displayName(i + "")
                        .uniqueName(i + "")
                        .build())
                .toList());
        // 카테고리 아이디 리스트 추출
        final List<Long> ids = categories.stream()
                .map(Category::getId)
                .toList();

        sut.execute(post.getId(), ids);

        //then
        final TemporalTechBlogPost assertPost = temporalTechBlogPostRepository.findById(post.getId())
                .orElseThrow(IllegalArgumentException::new);
        final List<Long> extractAssertPostCategoryIds = assertPost.getTemporalTechPostTags()
                .stream()
                .map(TemporalTechPostTag::getCategory)
                .map(Category::getId)
                .toList();
        // 가지고 있는 카테고리 아이디가 동일한지 검증
        assertThat(extractAssertPostCategoryIds).contains(ids.toArray(new Long[]{}));
        // 직접 등록한 시점에서 카테고리 등록이 완료된 상태로 변경되었는지 검증
        assertThat(assertPost.isRegistrationCompleted()).isTrue();
    }

    @Test
    void 존재하지_않는_카테고리를_태그로_등록할떄_오류가_발생합니다() {
        // given
        // 임시 기술블로그 데이터 생성
        final TemporalTechBlogPost post = temporalTechBlogPostRepository.save(TemporalTechBlogPost.builder()
                .author("author")
                .createdDate(LocalDate.now())
                .thumbnailUrl(null)
                .title("title")
                .summary("summary")
                .url("")
                .urlSuffix("suffix")
                .techBlogCode(TechBlogCode.BASE)
                .crawledDate(LocalDate.now())
                .build());
        // 카테고리 목록 생성
        final List<Category> categories = categoryRepository.saveAll(IntStream.rangeClosed(1, 3)
                .mapToObj(i -> Category.builder()
                        .displayName(i + "")
                        .uniqueName(i + "")
                        .build())
                .toList());

        final Long maxId = categories.stream().mapToLong(Category::getId).max().orElse(1L);
        assertThatThrownBy(() -> {
            // when & then
            // 등록된 카테고리 아이디에서 가장 마지막 아이디 부터 추가하여 테스트를 동작시켜 존재하지 않는 아이디를 만들어 냄
            sut.execute(post.getId(), LongStream.rangeClosed(maxId + 1, maxId + 3).boxed().toList());
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 게시글을_업데이트_할_때_기존_태그가_없어지면_삭제가_이루어진다() {
        // given
        // 임시 기술블로그 데이터 생성
        final TemporalTechBlogPost post = temporalTechBlogPostRepository.save(TemporalTechBlogPost.builder()
                .author("author")
                .createdDate(LocalDate.now())
                .thumbnailUrl(null)
                .title("title")
                .summary("summary")
                .url("")
                .urlSuffix("suffix")
                .techBlogCode(TechBlogCode.BASE)
                .crawledDate(LocalDate.now())
                .build());
        // 카테고리 목록 생성

        final List<Category> categories = categoryRepository.saveAll(IntStream.rangeClosed(1, 3)
                .mapToObj(i -> Category.builder()
                        .displayName(i + "")
                        .uniqueName(i + "")
                        .build())
                .toList());

        // 카테고리 아이디 리스트 추출
        final List<Long> ids = categories.stream()
                .map(Category::getId)
                .toList();

        sut.execute(post.getId(), ids);
        sut.execute(post.getId(), List.of(1L, 2L));

        final TemporalTechBlogPost updatedPost = temporalTechBlogPostRepository.findById(post.getId()).orElseThrow(IllegalArgumentException::new);
        temporalTechBlogPostRepository.flush();

        // Then
        assertThat(temporalTechPostTagRepository.findAll().size()).isEqualTo(2);
        assertThat(updatedPost.getTemporalTechPostTags().size()).isEqualTo(2);
    }

    @Test
    void 기존에는_게시글보다_하나_늘어나면_새로_추가됩니다() {
        // given
        // 임시 기술블로그 데이터 생성
        final TemporalTechBlogPost post = temporalTechBlogPostRepository.save(TemporalTechBlogPost.builder()
                .author("author")
                .createdDate(LocalDate.now())
                .thumbnailUrl(null)
                .title("title")
                .summary("summary")
                .url("")
                .urlSuffix("suffix")
                .techBlogCode(TechBlogCode.BASE)
                .crawledDate(LocalDate.now())
                .build());
        // 카테고리 목록 생성
        final List<Category> categories = categoryRepository.saveAll(IntStream.rangeClosed(1, 3)
                .mapToObj(i -> Category.builder()
                        .displayName(i + "")
                        .uniqueName(i + "")
                        .build())
                .toList());
        categoryRepository.save(Category.builder()
                .displayName("5")
                .uniqueName("5")
                .build());
        // 카테고리 아이디 리스트 추출
        final List<Long> ids = categories.stream()
                .map(Category::getId)
                .toList();

        sut.execute(post.getId(), ids);
        sut.execute(post.getId(), List.of(1L, 2L, 3L, 4L));

        final TemporalTechBlogPost updatedPost = temporalTechBlogPostRepository.findById(post.getId()).orElseThrow(IllegalArgumentException::new);
        temporalTechBlogPostRepository.flush();

        // Then
        assertThat(temporalTechPostTagRepository.findAll().size()).isEqualTo(4);
        assertThat(updatedPost.getTemporalTechPostTags().size()).isEqualTo(4);
    }

}