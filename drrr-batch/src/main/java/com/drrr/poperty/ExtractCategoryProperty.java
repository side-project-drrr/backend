package com.drrr.property;


import com.drrr.payload.common.Message;
import com.drrr.payload.request.OpenAiChatCompletionRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;


// prefix
@ConfigurationProperties("extract.category")
@ConfigurationPropertiesBinding
public record ExtractCategoryProperty(
        String gptKey,
        String apiUrl,
        String basePrompt,
        String model,
        String role
) {

    public OpenAiChatCompletionRequest createRequest(String content) {
        return new OpenAiChatCompletionRequest(
                model,
                Message.createSingle(role, basePrompt + content)
        );
    }

}
