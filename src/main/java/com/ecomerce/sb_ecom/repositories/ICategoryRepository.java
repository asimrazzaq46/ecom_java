package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryRepository extends JpaRepository<Category,Long> {
    Category findByCategoryName(String categoryName);
}
