package com.drrr.domain.techblogpost;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.util.ServiceIntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TechBlogPostCategoryFixture extends ServiceIntegrationTest {

    public static List<TechBlogPostCategory> createTechBlogPostCategory(final CategoryRepository categoryRepository,
                                                                        final TechBlogPostRepository techBlogPostRepository) {
        List<Category> categories = categoryRepository.findAll();
        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        List<TechBlogPostCategory> techBlogPostCategories = new ArrayList<>();

        posts.stream()
                .forEach(post -> {
                    List<TechBlogPostCategory> techBlogPostCategoryList = IntStream.range(0, categories.size())
                            .mapToObj(i -> TechBlogPostCategory.builder()
                                    .post(post)
                                    .category(categories.get(i))
                                    .build()).toList();
                    techBlogPostCategories.addAll(techBlogPostCategoryList);

                });
        return techBlogPostCategories;
    }
}
