package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.model.Category;

import java.util.List;


public interface ICategoryService {
    List<Category> getAllCategories();

    void createCategory(Category category);

    String deleteCategory(Long id);
    Category updateCategory(Category category,Long id);
}
