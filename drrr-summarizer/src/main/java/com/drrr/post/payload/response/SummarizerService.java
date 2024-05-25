package com.drrr.post.payload.response;


import com.drrr.post.payload.request.SummarizerRequest;
import com.drrr.post.provider.ExtractCategoryProvider;
import com.drrr.post.provider.SummarizeProvider;
import com.drrr.post.provider.TextLineSplitCategoryProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizerService {

    private final SummarizeProvider summarizeProvider;
    private final ExtractCategoryProvider extractCategoryProvider;
    private final TextLineSplitCategoryProvider textLineSplitCategoryProvider;

    public SummarizeResponse execute(SummarizerRequest summarizerRequest) {
        log.info("start summarize");
        final var summarizedBlogContent = summarizeProvider.execute(summarizerRequest.texts());
        log.info("extract category");
        final var extractedCategoryTexts = extractCategoryProvider.request(summarizedBlogContent).getFirstResult();
        log.info("split gpt result: {}", extractedCategoryTexts);
        final var categoryNames = textLineSplitCategoryProvider.execute(extractedCategoryTexts);

        log.info("success :{}", categoryNames);
        return new SummarizeResponse(
                summarizedBlogContent,
                categoryNames
        );
    }
}
