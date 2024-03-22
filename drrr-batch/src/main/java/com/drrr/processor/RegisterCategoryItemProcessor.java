package com.drrr.processor;

import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.service.RegisterPostTagService;
import com.drrr.provider.ExtractCategoryProvider;
import com.drrr.provider.SummarizeProvider;
import com.drrr.provider.TechBlogContentParserProvider;
import com.drrr.provider.TextLineSplitCategoryProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Slf4j

@Component
@RequiredArgsConstructor
public class RegisterCategoryItemProcessor implements ItemProcessor<TemporalTechBlogPost, TemporalTechBlogPost> {


    private final TechBlogContentParserProvider techBlogContentParserProvider;
    private final SummarizeProvider summarizeProvider;
    private final ExtractCategoryProvider extractCategoryProvider;
    private final TextLineSplitCategoryProvider textLineSplitCategoryProvider;
    private final RegisterPostTagService registerPostTagService;


    @Override
    public TemporalTechBlogPost process(final TemporalTechBlogPost temporalTechBlogPost) {
        final var blogContent = techBlogContentParserProvider.execute(
                temporalTechBlogPost.getTechBlogCode(),
                temporalTechBlogPost.getUrl()
        );
        final var summarizedBlogContent = summarizeProvider.execute(blogContent);
        final var extractedCategoryTexts = extractCategoryProvider.request(summarizedBlogContent).getFirstResult();
        final var categoryNames = textLineSplitCategoryProvider.execute(extractedCategoryTexts);

        registerPostTagService.execute(temporalTechBlogPost.getId(), categoryNames, summarizedBlogContent);

        // Todo: 트랜잭션 변경 상태 수정
        temporalTechBlogPost.transistorComplete();

        return temporalTechBlogPost;
    }
}
