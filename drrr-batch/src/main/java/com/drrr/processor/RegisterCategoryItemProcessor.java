package com.drrr.processor;

import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.service.RegisterPostTagService;
import com.drrr.provider.ExtractCategoryProvider;
import com.drrr.provider.SummarizeProvider;
import com.drrr.provider.TechBlogContentParserProvider;
import com.drrr.provider.TextLineSplitCategoryProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterCategoryItemProcessor implements ItemProcessor<TemporalTechBlogPost, TemporalTechBlogPost>,
        ItemWriter<TemporalTechBlogPost> {


    private final TechBlogContentParserProvider techBlogContentParserProvider;
    private final SummarizeProvider summarizeProvider;
    private final ExtractCategoryProvider extractCategoryProvider;
    private final TextLineSplitCategoryProvider textLineSplitCategoryProvider;
    private final RegisterPostTagService registerPostTagService;


    /**
     * @formatter:off
     * <p>
     * 1. 게시글의 본문을 가져옵니다.
     * 2. 게시글의 본문 내용을 요약합니다.
     * 3. 본문 내용을 바탕으로 카테고리를 추출합니다.
     * 4. 추출된 문자열을 카테고리 이름으로 변환합니다.
     * 5. 변환된 카테고리를 게시글에 등록합니다.
     * </p>
     * @formatter:on
     */
    @Override
    public TemporalTechBlogPost process(final TemporalTechBlogPost temporalTechBlogPost) {
        final var blogContent = techBlogContentParserProvider.execute(
                temporalTechBlogPost.getTechBlogCode(),
                temporalTechBlogPost.getUrl()
        );
        final var summarizedBlogContent = summarizeProvider.execute(blogContent);
        final var extractedCategoryTexts = extractCategoryProvider.request(summarizedBlogContent).getFirstResult();
        final var categoryNames = textLineSplitCategoryProvider.execute(extractedCategoryTexts);

        registerPostTagService.execute(
                temporalTechBlogPost.getId(),
                categoryNames,
                summarizedBlogContent
        );

        return temporalTechBlogPost;
    }

    @Override
    public void write(Chunk<? extends TemporalTechBlogPost> chunk) {
        chunk.forEach(this::process);
    }
}
