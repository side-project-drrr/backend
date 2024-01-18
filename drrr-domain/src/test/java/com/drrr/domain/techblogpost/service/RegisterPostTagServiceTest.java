package com.drrr.domain.techblogpost.service;

import static com.drrr.domain.techblogpost.service.RegisterPostTagServiceTest.RegisterPostTagFixture.category;
import static com.drrr.domain.techblogpost.service.RegisterPostTagServiceTest.RegisterPostTagFixture.categoryTypeIgnore;
import static com.drrr.domain.techblogpost.service.RegisterPostTagServiceTest.RegisterPostTagFixture.temporalTechBlogPost;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class RegisterPostTagServiceTest {

    @Autowired
    private RegisterPostTagService registerPostTagService;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;


    @Test
    void 카테고리_등록_기능_테스트() {

        final var id = temporalTechBlogPostRepository.save(temporalTechBlogPost()).getId();
        categoryRepository.save(category("카테고리1"));
        categoryRepository.save(category("카테고리2"));

        registerPostTagService.execute(id, List.of("카테고리1", "카테고리2"), "");

        final var temporalTechBlogPost = temporalTechBlogPostRepository.findById(id).orElseThrow();
        assertThat(temporalTechBlogPost.getTemporalTechPostTags().size()).isEqualTo(2);
    }

    @Test
    void 카테고리_무시_태그의_경우_등록된_게시글이_없습니다() {
        final var id = temporalTechBlogPostRepository.save(temporalTechBlogPost()).getId();
        categoryRepository.save(categoryTypeIgnore("카테고리1"));
        categoryRepository.save(categoryTypeIgnore("카테고리2"));

        registerPostTagService.execute(id, List.of("카테고리1", "카테고리2"), "");

        final var temporalTechBlogPost = temporalTechBlogPostRepository.findById(id).orElseThrow();
        assertThat(temporalTechBlogPost.getTemporalTechPostTags().size()).isEqualTo(0);
    }

    @Test
    void 대체_유형의_경우_정상적으로_변환됩니다() {
        final var id = temporalTechBlogPostRepository.save(temporalTechBlogPost()).getId();
        final var category1 = categoryRepository.save(category("카테고리1"));
        final var category2 = categoryRepository.save(category("카테고리2"));

        category1.changeReplaceType(category2.getId());

        registerPostTagService.execute(id, List.of("카테고리1", "카테고리2"), "");

        final var temporalTechBlogPost = temporalTechBlogPostRepository.findById(id).orElseThrow();
        assertThat(temporalTechBlogPost.getTemporalTechPostTags().size()).isEqualTo(1);
    }


    record RegisterPostTagFixture() {

        public static TemporalTechBlogPost temporalTechBlogPost() {
            return TemporalTechBlogPost.builder()
                    .title("title")
                    .author("author")
                    .techBlogCode(TechBlogCode.BASE)
                    .crawledDate(LocalDate.now())
                    .createdDate(LocalDate.now())
                    .registrationCompleted(false)
                    .urlSuffix("123")
                    .url(" ")
                    .build();
        }

        public static Category category(String name) {
            return new Category(name);
        }

        public static Category categoryTypeIgnore(String name) {
            final var category = new Category(name);
            category.changeIgnoreType();
            return category;
        }
    }
}