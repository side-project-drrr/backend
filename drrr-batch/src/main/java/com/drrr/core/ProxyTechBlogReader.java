package com.drrr.core;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.fluent.TechBlogReader;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProxyTechBlogReader implements TechBlogReader {
    private final Supplier<TechBlogReader> techBlogReaderSupplier;
    private final TechBlogCode code;
    private TechBlogReader techBlogReader;

    @Override
    public TechBlogCode getTechBlogCode() {
        return this.code;
    }

    @Override
    public ExternalBlogPosts read() throws Exception {
        if (Objects.isNull(techBlogReader)) {
            this.techBlogReader = techBlogReaderSupplier.get();
        }
        return this.techBlogReader.read();
    }
}
