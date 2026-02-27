package com.supermarket.supermarketinventory.service;
import com.supermarket.supermarketinventory.entity.Category;
import java.util.List;

public interface CategoryService {
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Long id);
    List<Category> getAll();
}
