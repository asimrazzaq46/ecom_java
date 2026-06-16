package com.ecomerce.sb_ecom.interfaces;

import com.ecomerce.sb_ecom.payload.category.CategoryDto;
import com.ecomerce.sb_ecom.payload.category.CategoryResponse;


public interface ICategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDto createCategory(CategoryDto category);

    CategoryDto deleteCategory(Long id);

    CategoryDto updateCategory(CategoryDto category, Long id);
}
