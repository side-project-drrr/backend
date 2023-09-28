package com.drrr.domain.techblogpost.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.core.code.TechBlogCode;
import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.annotation.EnableMysqlProfile;
import com.drrr.domain.annotation.EnableRepositoryTest;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechPostTagRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@EnableRepositoryTest
@EnableMysqlProfile
class TemporalTechBlogPostRepositoryImplTest {
    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TemporalTechPostTagRepository temporalTechPostTagRepository;

    /**
     * <h3>Given</h3>
     * <br>2개의 카테고리(Java Node.js)가 등록되어 있습니다
     * <br> 2개의 게시글이 등록되어 있습니다.
     * <br> 여기서 게시글 하나는 현재 일자 기준으로 하루 전에 등록된 게시글이고
     * <br> 다른 하나는 현재 일자 기준으로 등록된 데이터 입니다.
     * <br> 게시글의 카테고리는 오직 1번 게시글에만 등록되어 있으며 (JAVA,Node.js) 두 카테고리를 가지고 있습니다.
     */
    @BeforeEach
    void setup() {
        final var JAVACategory = categoryRepository.saveAndFlush(Category.builder()
                .displayName("자바")
                .uniqueName("JAVA")
                .build());
        final var NodeCategory = categoryRepository.save(Category.builder()
                .displayName("node js")
                .uniqueName("NODE_JS")
                .build());

        final var post = temporalTechBlogPostRepository.saveAndFlush(TemporalTechBlogPost.builder()
                .author("author")
                .createdDate(LocalDate.now())
                .thumbnailUrl(null)
                .title("title")
                .summary("summary")
                .url("")
                .urlSuffix("suffix")
                .techBlogCode(TechBlogCode.BASE)
                .crawledDate(LocalDate.now().minusDays(1))
                .build());

        final var temporalTechPostTag = temporalTechPostTagRepository.saveAll(List.of(
                new TemporalTechPostTag(JAVACategory, post),
                new TemporalTechPostTag(NodeCategory, post)
        ));

        post.registerCategory(temporalTechPostTag);

        temporalTechBlogPostRepository.save(TemporalTechBlogPost.builder()
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
    }

    @Test
    void 기간_상관없이_전체_게시글을_가져옵니다() {
        final var result = temporalTechBlogPostRepository.findBy(null, (Boolean) null);

        assertThat(result).size().isEqualTo(2);
    }


    @Test
    void 크롤링한_날짜_기준으로_하루_전_게시글을_조회한다() {
        // 현재 일자 기준으로 1~2일 지난 게시글 조회
        final var result = temporalTechBlogPostRepository.findBy(DateRangeBound.builder()
                .startDate(LocalDate.now().minusDays(1))
                .lastDate(LocalDate.now().minusDays(1))
                .build(), null);

        assertThat(result).size().isEqualTo(1);
    }

    @Test
    void 크롤링된_게시글을_가져올_때_카테고리_또한_가져온다() {
        final var result = temporalTechBlogPostRepository.findBy(
                DateRangeBound.createSingleRange(LocalDate.now().minusDays(1)), null);

        assertThat(result).size().isEqualTo(1);
        assertThat(result.get(0).getTemporalTechPostTags()).size().isEqualTo(2);
    }

    @Test
    void 크롤링된_게시글의_태그가_등록이_완료된_게시글만_조회한다() {
        final List<TemporalTechBlogPost> temporalTechBlogPosts = temporalTechBlogPostRepository.findBy(null, true);
        assertThat(temporalTechBlogPosts).size().isEqualTo(1);
    }
}