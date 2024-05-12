package com.drrr.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.drrr.config.util.SpringBatchTestSupport;
import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.service.RegisterPostTagService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("마이그레이션 통합 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MigrationBatchConfigurationTest extends SpringBatchTestSupport {

    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;

    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategory;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;

    @Autowired
    private RegisterPostTagService registerPostTagService;

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


    @Test
    void 기술블로그가_정상적으로_마이그레이션_처리됩니다() {

        final var id = this.temporalTechBlogPostRepository.save(temporalTechBlogPost()).getId();
        registerPostTagService.execute(id, List.of("카테고리1", "카테고리2"), "");

        this.launchJob(MigrationBatchConfiguration.JOB_NAME, new JobParametersBuilder()
                .addLong("techBlogCode", TechBlogCode.BASE.getId())
                .toJobParameters());

        assertAll(
                () -> assertThat(this.techBlogPostRepository.count()).isEqualTo(1),
                () -> assertThat(this.techBlogPostCategory.count()).isEqualTo(2),
                () -> assertThat(this.temporalTechBlogPostRepository.count()).isEqualTo(0)
        );


    }

}