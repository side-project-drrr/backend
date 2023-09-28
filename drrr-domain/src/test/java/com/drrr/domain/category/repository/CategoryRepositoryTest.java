package com.drrr.domain.category.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.domain.annotation.EnableRepositoryTest;
import com.drrr.domain.category.entity.Category;
import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;


@EnableRepositoryTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void 카테고리를_조회할때_아이디_목록으로_조회한다() {
        categoryRepository.saveAllAndFlush(IntStream.rangeClosed(1, 3)
                .mapToObj(i -> Category.builder()
                        .displayName(i + "")
                        .uniqueName(i + "")
                        .build())
                .toList());

        final int count = categoryRepository.findIds(List.of(1L, 2L, 3L)).size();

        assertThat(count).isEqualTo(3);
    }

    @Test
    void 카테고리의_고유_이름_혹은_표시되는_이름으로_검색할_수_있습니다() {
        final List<Category> categories = IntStream.iterate(1, i -> i + 1)
                .limit(10)
                .mapToObj(i -> Category.builder()
                        .displayName("자바" + i)
                        .uniqueName("다른거" + i)
                        .build())
                .toList();

        categoryRepository.saveAll(categories);

        final List<Category> result = categoryRepository.findByUniqueNameOrDisplayNameContaining("자", Pageable
                .ofSize(10)
        );

        Assertions.assertThat(result).hasSize(10);

    }

}