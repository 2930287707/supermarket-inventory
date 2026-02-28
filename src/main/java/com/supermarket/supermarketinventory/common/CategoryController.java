package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.entity.Category;
import com.supermarket.supermarketinventory.logging.OperationLog;
import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.CategoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin
@RequireRole({"ADMIN", "MANAGER", "STAFF"})
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.getAll());
    }

    @PostMapping("/add")
    @RequireRole({"ADMIN", "MANAGER"})
    @OperationLog("新增分类")
    public Result<Void> add(@RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success();
    }

    @PostMapping("/update")
    @RequireRole({"ADMIN", "MANAGER"})
    @OperationLog("修改分类")
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @RequireRole({"ADMIN"})
    @OperationLog("删除分类")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
