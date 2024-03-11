package com.drrr.domain.techblogpost;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.util.ServiceIntegrationTest;
import java.util.ArrayList;
import java.util.List;

public class TechBlogPostCategoryFixture extends ServiceIntegrationTest {

    public static List<TechBlogPostCategory> createTechBlogPostCategory(final List<TechBlogPost> posts,
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
}
