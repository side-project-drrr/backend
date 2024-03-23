package com.drrr.provider;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.drrr.property.TextSummarizerProperty;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummarizeProvider {

    private final ChildProcessRunner childProcessRunner;
    private final TextSummarizerProperty textSummarizerProperty;


    public String execute(List<String> texts) {
        var executor = Executors.newFixedThreadPool(textSummarizerProperty.calculateRunnerCount(texts.size()));

        var completableFutures = texts.stream()
                .map((text) -> supplyAsync(() -> this.executePythonScript(text), executor))
                .toList();

        return completableFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.joining());
    }

    private String executePythonScript(String args) {
        log.info("{}", textSummarizerProperty);
        return this.childProcessRunner.execute(
                "python",
                textSummarizerProperty.scriptPath(),
                args
        );
    }

}
