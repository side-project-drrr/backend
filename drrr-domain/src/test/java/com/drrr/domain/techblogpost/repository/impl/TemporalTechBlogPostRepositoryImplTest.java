package com.drrr.domain.techblogpost.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.TechBlogCode;
import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.annotation.EnableRepositoryTest;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@EnableRepositoryTest
class TemporalTechBlogPostRepositoryImplTest {
    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TemporalTechPostTagRepository temporalTechPostTagRepository;

    @Test
    void 크롤링된_당일_게시글을_정상적으로_불러옵니다() {
        final var JAVACategory = categoryRepository.saveAndFlush(Category.builder()
                .displayName("자바")
                .uniqueName("JAVA")
                .build());

        final var post1 = temporalTechBlogPostRepository.saveAndFlush(TemporalTechBlogPost.builder()
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

        final var temporalTechPostTag = temporalTechPostTagRepository.saveAllAndFlush(List.of(new TemporalTechPostTag(JAVACategory, post1)));
        post1.registerCategory(temporalTechPostTag);

        final var result = temporalTechBlogPostRepository.findBy(DateRangeBound.builder()
                .startDate(LocalDate.now())
                .lastDate(LocalDate.now())
                .build()
        );

        assertThat(post1).isEqualTo(result.get(0));
        assertThat(post1.getTemporalTechPostTags().get(0)).isEqualTo(temporalTechPostTag.get(0));
    }


    @Test
    void 크롤링된_게시글_중에서_기간내_존재하지_않는_글은_가져오지_않는다() {
        final var JAVACategory = categoryRepository.save(Category.builder()
                .displayName("자바")
                .uniqueName("JAVA")
                .build());

        final var NodeCategory = categoryRepository.save(Category.builder()
                .displayName("node js")
                .uniqueName("NODE_JS")
                .build());

        // 당일 게시글
        final var post1 = temporalTechBlogPostRepository.save(TemporalTechBlogPost.builder()
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

        final var temporalNodeTag = temporalTechPostTagRepository.save(new TemporalTechPostTag(NodeCategory, post1));
        final var temporalJavaTag = temporalTechPostTagRepository.save(new TemporalTechPostTag(JAVACategory, post1));

        post1.registerCategory(List.of(temporalNodeTag, temporalJavaTag));

        // 현재 일자 기준으로 1~2일 지난 게시글 조회
        final var result = temporalTechBlogPostRepository.findBy(DateRangeBound.builder()
                .startDate(LocalDate.now().minusDays(2))
                .lastDate(LocalDate.now().minusDays(1))
                .build());

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void 크롤링된_게시글_중에서_카테고리가_여러개_인_글을_긁어온다() {
        final var JAVACategory = categoryRepository.save(Category.builder()
                .displayName("자바")
                .uniqueName("JAVA")
                .build());

        final var NodeCategory = categoryRepository.save(Category.builder()
                .displayName("node js")
                .uniqueName("NODE_JS")
                .build());

        // 당일 게시글
        final var post1 = temporalTechBlogPostRepository.save(TemporalTechBlogPost.builder()
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

        final var temporalNodeTag = temporalTechPostTagRepository.save(new TemporalTechPostTag(NodeCategory, post1));
        final var temporalJavaTag = temporalTechPostTagRepository.save(new TemporalTechPostTag(JAVACategory, post1));

        post1.registerCategory(List.of(temporalNodeTag, temporalJavaTag));

        // 현재 일자 기준으로 1~2일 지난 게시글 조회
        final var result = temporalTechBlogPostRepository.findBy(DateRangeBound.createSingleRange(LocalDate.now()));

        Assertions.assertThat(result).size().isEqualTo(1);
        Assertions.assertThat(result.get(0).getTemporalTechPostTags()).size().isEqualTo(2);
    }
}