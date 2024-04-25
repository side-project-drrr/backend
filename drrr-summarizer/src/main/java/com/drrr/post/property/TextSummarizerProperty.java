package com.drrr.post.property;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

@ConfigurationProperties("summarizer")
@ConfigurationPropertiesBinding
public record TextSummarizerProperty(int runnerCount, String scriptPath) {
    public int calculateRunnerCount(int runnerCount) {
        return Math.min(this.runnerCount, runnerCount);
    }
}
