package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.ApiException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.model.Category;
import com.ecomerce.sb_ecom.repositories.ICategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {


    @Autowired
    private ICategoryRepository categoryRepository;


    @Override
    public List<Category> getAllCategories() {
        var categories = categoryRepository.findAll();
        if(categories.size() <= 0) throw new ApiException("No categories found.");
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory != null) throw new ApiException("Category with the name \"" + category.getCategoryName() + "\" already exists !!");
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long id) {
        var cate = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",id));

        categoryRepository.delete(cate);
        return "category with categoryID: " + id + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        var cate = categoryRepository.findById(id);

        // return type is optional so if it's null than we can throw the exception
        var exsistingcategory = cate.orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",id));

        exsistingcategory.setCategoryName(category.getCategoryName());
        // we return the category because jpa method save returns the updated entity
        return categoryRepository.save(exsistingcategory);


    }
}
