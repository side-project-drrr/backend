package com.drrr.domain.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MapperUtils {
    private final ObjectMapper objectMapper;

    public JavaType mapType(final Class<? extends Collection> collectionClass, final Class<?> elementClass) {
        return objectMapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public JavaType mapType(final Class<?> elementClass) {
        return objectMapper.getTypeFactory().constructType(elementClass);
    }
}
