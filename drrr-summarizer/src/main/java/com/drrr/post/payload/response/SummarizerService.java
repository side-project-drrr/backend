package com.drrr.post.payload.response;


import com.drrr.post.payload.request.SummarizerRequest;
import com.drrr.post.provider.ExtractCategoryProvider;
import com.drrr.post.provider.SummarizeProvider;
import com.drrr.post.provider.TextLineSplitCategoryProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummarizerService {

    private final SummarizeProvider summarizeProvider;
    private final ExtractCategoryProvider extractCategoryProvider;
    private final TextLineSplitCategoryProvider textLineSplitCategoryProvider;

    public SummarizeResponse execute(SummarizerRequest summarizerRequest) {
        final var summarizedBlogContent = summarizeProvider.execute(summarizerRequest.texts());
        final var extractedCategoryTexts = extractCategoryProvider.request(summarizedBlogContent).getFirstResult();
        final var categoryNames = textLineSplitCategoryProvider.execute(extractedCategoryTexts);

        return new SummarizeResponse(
                summarizedBlogContent,
                categoryNames
        );
    }
}
