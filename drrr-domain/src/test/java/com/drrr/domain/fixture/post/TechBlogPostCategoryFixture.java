package com.drrr.domain.fixture.post;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import java.util.ArrayList;
import java.util.List;

public class TechBlogPostCategoryFixture {

    public static List<TechBlogPostCategory> createTechBlogPostCategories(final List<TechBlogPost> posts,
                                                                          final List<Category> categories) {

        List<TechBlogPostCategory> techBlogPostCategories = new ArrayList<>();

        posts.forEach(post -> {
            List<TechBlogPostCategory> techBlogPostCategoryList = categories.stream()
                    .map(category -> TechBlogPostCategory.builder()
                            .post(post)
                            .category(category)
                            .build()).toList();
            techBlogPostCategories.addAll(techBlogPostCategoryList);
        });
        return techBlogPostCategories;
    }

    public static List<TechBlogPostCategory> createTechBlogPostCategories(final List<TechBlogPost> posts,
                                                                          final Category category) {

        List<TechBlogPostCategory> techBlogPostCategories = new ArrayList<>();

        posts.forEach(post -> {
            TechBlogPostCategory techBlogPostCategoryList = TechBlogPostCategory.builder()
                    .post(post)
                    .category(category)
                    .build();
            techBlogPostCategories.add(techBlogPostCategoryList);
        });
        return techBlogPostCategories;
    }

    public static List<TechBlogPostCategory> createTechBlogPostCategories(final TechBlogPost post,
                                                                          final List<Category> categories) {
        return categories.stream()
                .map(category -> TechBlogPostCategory.builder()
                        .post(post)
                        .category(category)
                        .build()).toList();
    }

    public static TechBlogPostCategory createTechBlogPostCategory(final TechBlogPost post, final Category category) {
        return TechBlogPostCategory.builder()
                .post(post)
                .category(category)
                .build();
    }

    public static List<TechBlogPostCategory> createTechBlogPostCategories(
            final List<TechBlogPost> selectedTechBlogPosts,
            final List<TechBlogPost> otherTechBlogPosts,
            final List<Category> categories) {
        List<TechBlogPostCategory> techBlogPostCategories = new ArrayList<>();

        selectedTechBlogPosts.forEach(post -> {
            List<TechBlogPostCategory> techBlogPostCategoryList = categories.stream()
                    .map(category -> TechBlogPostCategory.builder()
                            .post(post)
                            .category(category)
                            .build()).toList();
            techBlogPostCategories.addAll(techBlogPostCategoryList);
        });
        otherTechBlogPosts.forEach(post -> {
            List<TechBlogPostCategory> techBlogPostCategoryList = categories.stream()
                    .map(category -> TechBlogPostCategory.builder()
                            .post(post)
                            .category(category)
                            .build()).toList();
            techBlogPostCategories.addAll(techBlogPostCategoryList);
        });
        return techBlogPostCategories;
    }
}
