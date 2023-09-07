package com.drrr.domain.category.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.domain.annotation.EnableRepositoryTest;
import com.drrr.domain.category.entity.Category;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


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

}