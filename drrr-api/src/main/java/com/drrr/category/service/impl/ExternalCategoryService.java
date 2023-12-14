package com.drrr.category.service.impl;

import com.drrr.category.dto.CategoryRequest;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.service.CategoryService;
import com.drrr.domain.category.service.CategoryService.CategoryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalCategoryService {
    private final CategoryService categoryService;
    public List<CategoryDto> execute(){
        return categoryService.findAllCategories();
    }

    public List<CategoryDto> execute(CategoryRequest request){
        return categoryService.findSelectedCategories(request.categoryIds());
    }
}
