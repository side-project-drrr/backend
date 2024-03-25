package com.drrr.fluent.cralwer.core;


/**
 * 크롤링 하는 최소 단위
 *
 *
 * @formatter:off
 * <p>
 *     *MultiPage
 *     *SinglePage
 *     *ScrollPage
 * </p>
 *
 * @formatterr:on
 */
public interface Page<T> {
    T execute();
}
