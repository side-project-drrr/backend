package com.drrr.techblogpost.service;

import com.drrr.domain.category.service.CategoryService.CategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    public List<TechBlogPost> execute() {
        return techBlogPostService.findAllPosts();
    }
    public List<TechBlogPost> execute(Long id) {
        return techBlogPostService.findPostsByCategory(id);
    }
}
