package com.supermarket.supermarketinventory.common;
import com.supermarket.supermarketinventory.entity.Category;
import com.supermarket.supermarketinventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.getAll());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Category category) {
        try {
            categoryService.addCategory(category);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody Category category) {
        try {
            categoryService.updateCategory(category);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }
}
