package com.drrr.fluent.cralwer.core;


import java.time.Duration;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@RequiredArgsConstructor
public class SinglePage<T> implements Page<T> {

    private final SinglePageInitializer singlePageInitializer;
    private final WebDriver webDriver;
    private final WebDriverWait webDriverWait;
    private final ContentsLoader contentsLoader;
    private final ContentsReader<T> contentsReader;
    private final After<T> after;

    private Mode mode = Mode.ONCE;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private boolean completed = false;


    @Builder
    public SinglePage(
            SinglePageInitializer singlePageInitializer,
            WebDriver webDriver,
            ContentsLoader contentsLoader,
            ContentsReader<T> contentsReader,
            Mode mode,
            After<T> after
    ) {
        Objects.requireNonNull(singlePageInitializer);
        Objects.requireNonNull(contentsReader);
        Objects.requireNonNull(webDriver);
        Objects.requireNonNull(contentsLoader);

        this.singlePageInitializer = singlePageInitializer;
        this.webDriver = webDriver;
        this.webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        this.contentsLoader = contentsLoader;
        this.contentsReader = contentsReader;
        this.mode = mode;
        this.after = after;
    }

    @Override
    public T execute() {
        // ONCE 모드의 경우 한 번 호출 된 이후 재호출을 금지합니다.
        if (mode == Mode.ONCE && isCompleted()) {
            return null;
        }

        webDriver.get(singlePageInitializer.getUrl());
        contentsLoader.waitUntilLoad(webDriverWait);

        final var contents = contentsReader.read(webDriver);

        if (Objects.nonNull(after)) {
            after.action(contents);
        }

        done();
        cleanup(webDriver);

        return contents;
    }

    private void done() {
        if (mode == Mode.ONCE) {
            setCompleted(true);
        }
    }

    public enum Mode {
        ONCE,
        NON_LIMIT
    }
}
