package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.ApiException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.interfaces.ICategoryService;
import com.ecomerce.sb_ecom.interfaces.IPaginationService;
import com.ecomerce.sb_ecom.model.Category;
import com.ecomerce.sb_ecom.payload.category.CategoryDto;
import com.ecomerce.sb_ecom.payload.category.CategoryResponse;
import com.ecomerce.sb_ecom.repositories.ICategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IPaginationService paginationService;


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetail = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetail);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) throw new ApiException("No categories created till now.");

        // with the use of modelMapper converting it inTo DTO
        List<CategoryDto> categoryDto = categories.stream()
                .map(cat -> modelMapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDto);
        categoryResponse.setPageDetail(paginationService.getPageInfo(categoryPage));
        return categoryResponse;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        Category isCategoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if (isCategoryFromDb != null)
            throw new ApiException("Category with the name \"" + category.getCategoryName() + "\" already exists !!");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto deleteCategory(Long id) {
        var cate = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", id));

        var categoryDto = modelMapper.map(cate, CategoryDto.class);

        categoryRepository.delete(cate);
        return categoryDto;
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {
        var category = modelMapper.map(categoryDto, Category.class);
        var cate = categoryRepository.findById(id);

        // return type is optional so if it's null than we can throw the exception
        var exsistingcategory = cate.orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", id));

        exsistingcategory.setCategoryName(category.getCategoryName());
        // we return the category because jpa method save returns the updated entity
        var savedCategory = categoryRepository.save(exsistingcategory);

        return modelMapper.map(savedCategory, CategoryDto.class);
    }
}
