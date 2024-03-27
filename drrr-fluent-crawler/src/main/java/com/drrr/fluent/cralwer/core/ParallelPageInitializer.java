package com.drrr.fluent.cralwer.core;

import java.util.function.Function;

public interface ParallelPageInitializer {

    BasePageUrls getUrl();

    record BasePageUrls(
            String home,
            Function<Integer, String> searchUrlGenerator
    ) {
    }


}
