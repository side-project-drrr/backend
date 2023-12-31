package com.drrr.core;

import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;


@RequiredArgsConstructor
public class ProxyItemReader<T> implements ItemReader<T> {
    private final Supplier<ItemReader<T>> itemReaderSupplier;
    private ItemReader<T> lazyItemReader;

    @Override
    public T read() throws Exception {
        if (Objects.isNull(lazyItemReader)) {
            lazyItemReader = itemReaderSupplier.get();
        }
        return lazyItemReader.read();
    }
}
