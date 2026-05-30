package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.ApiException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.model.Category;
import com.ecomerce.sb_ecom.payload.CategoryDto;
import com.ecomerce.sb_ecom.payload.CategoryResponse;
import com.ecomerce.sb_ecom.repositories.ICategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()) throw new ApiException("No categories created till now.");

        // with the use of modelMapper converting it inTo DTO
        List<CategoryDto> categoryDto = categories.stream()
                .map(cat -> modelMapper.map(cat,CategoryDto.class))
                .collect(Collectors.toList());

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDto);
        return categoryResponse;
    }

    @Override
    public void createCategory(CategoryDto categoryDto) {
        Category savedCategory = categoryRepository.findByCategoryName(categoryDto.getCategoryName());
        if(savedCategory != null) throw new ApiException("Category with the name \"" + categoryDto.getCategoryName() + "\" already exists !!");
       Category category = modelMapper.map(categoryDto,Category.class);
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
    public CategoryDto updateCategory(CategoryDto category, Long id) {
        var cate = categoryRepository.findById(id);

        // return type is optional so if it's null than we can throw the exception
        var exsistingcategory = cate.orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",id));

        exsistingcategory.setCategoryName(category.getCategoryName());
        // we return the category because jpa method save returns the updated entity
        return categoryRepository.save(exsistingcategory);


    }
}
