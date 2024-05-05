package com.drrr.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.drrr.config.util.SpringBatchTestSupport;
import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import com.drrr.reader.CrawlerItemReaderFactory;
import com.drrr.repository.CrawledTechBlogPostRepository;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;


@DisplayName("크롤링 + 저장 연산 통합 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WebCrawlerBatchConfigurationTest extends SpringBatchTestSupport {


    List<ExternalBlogPost> mockExternalBlogPosts;
    @MockBean
    private CrawlerItemReaderFactory crawlerItemReaderFactory;
    @Autowired
    private CrawledTechBlogPostRepository crawledTechBlogPostRepository;
    @Autowired
    private TemporalTechBlogPostRepository temporalTechBlogPostRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;


    private List<ExternalBlogPosts> generateMockData() {
        this.mockExternalBlogPosts = IntStream.iterate(1, i -> i + 1)
                .boxed()
                .map(i -> ExternalBlogPost.builder()
                        .author("author" + i)
                        .code(TechBlogCode.BASE)
                        .title("title" + i)
                        .link("link" + i)
                        .suffix("suffix" + i)
                        .postDate(LocalDate.now())
                        .summary("summary" + i)
                        .thumbnailUrl("thumbnailUrl" + i)
                        .build()
                ).limit(100)
                .toList();
        return List.of(new ExternalBlogPosts(mockExternalBlogPosts));
    }

    @Test
    void 기술블로그_혹은_임시기술블로그_모두_데이터가_없는_상태에는_전체저장_됩니다() {
        // given
        given(crawlerItemReaderFactory.createItemReader(TechBlogCode.BASE))
                .willReturn(new FakeItemReader(generateMockData()));
        // when
        this.launchJob(WebCrawlerBatchConfiguration.JOB_NAME, new JobParametersBuilder()
                .addLong("techBlogCode", TechBlogCode.BASE.getId())
                .toJobParameters());

        // then
        assertThat(crawledTechBlogPostRepository.count()).isEqualTo(100);
        assertThat(temporalTechBlogPostRepository.count()).isEqualTo(100);
    }

    @Test
    void 기술블로그에_이미_크롤링한_동일한_데이터가_있는_경우_저장되는_데이터는_없습니다() {
// given
        given(crawlerItemReaderFactory.createItemReader(TechBlogCode.BASE))
                .willReturn(new FakeItemReader(generateMockData()));

        techBlogPostRepository.saveAll(this.mockExternalBlogPosts.stream()
                .map(externalBlogPost -> TechBlogPost.builder()
                        .title(externalBlogPost.title())
                        .author(externalBlogPost.author())
                        .crawlerGroup(externalBlogPost.code())
                        .url("url")
                        .writtenAt(externalBlogPost.postDate())
                        .summary(externalBlogPost.summary())
                        .urlSuffix(externalBlogPost.suffix())
                        .build())
                .toList());

        // when
        this.launchJob(WebCrawlerBatchConfiguration.JOB_NAME, new JobParametersBuilder()
                .addLong("techBlogCode", TechBlogCode.BASE.getId())
                .toJobParameters());

        // then
        assertThat(crawledTechBlogPostRepository.count()).isEqualTo(0);
        assertThat(techBlogPostRepository.count()).isEqualTo(100);
    }

    @Test
    void 임시기술블로그에_이미_크롤링한_동일한_데이터가_있는_경우_저장되는_데이터는_없습니다() {
// given
        given(crawlerItemReaderFactory.createItemReader(TechBlogCode.BASE))
                .willReturn(new FakeItemReader(generateMockData()));

        temporalTechBlogPostRepository.saveAll(this.mockExternalBlogPosts.stream()
                .map(externalBlogPost -> TemporalTechBlogPost.builder()
                        .title(externalBlogPost.title())
                        .author(externalBlogPost.author())
                        .techBlogCode(externalBlogPost.code())
                        .crawledDate(LocalDate.now())
                        .createdDate(externalBlogPost.postDate())
                        .url("url")
                        .summary(externalBlogPost.summary())
                        .urlSuffix(externalBlogPost.suffix())
                        .build())
                .toList());

        // when
        this.launchJob(WebCrawlerBatchConfiguration.JOB_NAME, new JobParametersBuilder()
                .addLong("techBlogCode", TechBlogCode.BASE.getId())
                .toJobParameters());

        // then
        assertThat(crawledTechBlogPostRepository.count()).isEqualTo(0);
        assertThat(temporalTechBlogPostRepository.count()).isEqualTo(100);
    }

    @Test
    void 기술블로그에_등록된_데이터가_크롤링된_데이터에_없으면_삭제_됩니다() {
        // given
        given(crawlerItemReaderFactory.createItemReader(TechBlogCode.BASE))
                .willReturn(new FakeItemReader());
        generateMockData();
        techBlogPostRepository.saveAll(this.mockExternalBlogPosts.stream()
                .map(externalBlogPost -> TechBlogPost.builder()
                        .title(externalBlogPost.title())
                        .author(externalBlogPost.author())
                        .crawlerGroup(externalBlogPost.code())
                        .writtenAt(externalBlogPost.postDate())
                        .url("url")
                        .summary(externalBlogPost.summary())
                        .urlSuffix(externalBlogPost.suffix())
                        .build())
                .toList());

        // when
        this.launchJob(WebCrawlerBatchConfiguration.JOB_NAME, new JobParametersBuilder()
                .addLong("techBlogCode", TechBlogCode.BASE.getId())
                .toJobParameters());

        // then
        assertThat(crawledTechBlogPostRepository.count()).isEqualTo(0);
        assertThat(temporalTechBlogPostRepository.count()).isEqualTo(0);
        assertThat(techBlogPostRepository.count()).isEqualTo(0);
    }

    public static class FakeItemReader implements ItemReader<ExternalBlogPosts> {
        private final Queue<ExternalBlogPosts> posts;

        public FakeItemReader(List<ExternalBlogPosts> externalBlogPosts) {
            this.posts = new LinkedList<>(externalBlogPosts);
        }

        public FakeItemReader() {
            this.posts = new LinkedList<>();
        }

        @Override
        public ExternalBlogPosts read() {
            return posts.poll();
        }
    }

}