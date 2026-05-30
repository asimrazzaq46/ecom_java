package com.ecomerce.sb_ecom.controller;

import com.ecomerce.sb_ecom.payload.CategoryDto;
import com.ecomerce.sb_ecom.payload.CategoryResponse;
import com.ecomerce.sb_ecom.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {


    private ICategoryService categoryService;

    @Autowired
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    private ResponseEntity<CategoryResponse> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryDto category) {
        categoryService.createCategory(category);
        return new ResponseEntity<>("Category added Successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
            var status = categoryService.deleteCategory(id);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{id}")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody CategoryDto category, @PathVariable Long id) {
            var categoryInDb = categoryService.updateCategory(category,id);
            return new ResponseEntity<>("Category with id: " + id + " updated", HttpStatus.OK);
    }

}


