package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.payload.CategoryDto;
import com.ecomerce.sb_ecom.payload.CategoryResponse;


public interface ICategoryService {
    CategoryResponse getAllCategories();

    void createCategory(CategoryDto category);

    String deleteCategory(Long id);
    CategoryDto updateCategory(CategoryDto category,Long id);
}
