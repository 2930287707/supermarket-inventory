package com.supermarket.supermarketinventory.service.impl;
import com.supermarket.supermarketinventory.entity.Category;
import com.supermarket.supermarketinventory.mapper.CategoryMapper;
import com.supermarket.supermarketinventory.mapper.GoodsMapper;
import com.supermarket.supermarketinventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private GoodsMapper goodsMapper; // 注入 GoodsMapper

    @Override
    public void addCategory(Category category) {
        Category exist = categoryMapper.selectByName(category.getName());
        if (exist != null) {
            throw new RuntimeException("分类名称已存在");
        }
        if(category.getSortOrder() == null) category.setSortOrder(0);
        categoryMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // ★★★ 必须开启事务，保证两张表同时更新成功
    public void updateCategory(Category category) {
        // 1. 获取旧数据
        Category oldCategory = categoryMapper.selectById(category.getId());
        if (oldCategory == null) {
            throw new RuntimeException("该分类不存在");
        }

        // 2. 检查名称是否被占用（如果是改名的话）
        if (!oldCategory.getName().equals(category.getName())) {
            Category nameOccupied = categoryMapper.selectByName(category.getName());
            if (nameOccupied != null) {
                throw new RuntimeException("修改失败：分类名 ["+category.getName()+"] 已存在");
            }

            // 3.  核心联动：更新所有使用该分类的商品
            goodsMapper.updateCategoryName(oldCategory.getName(), category.getName());
        }

        categoryMapper.update(category);
    }
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) return;

        // 1. 严格校验：如果分类下有商品，禁止删除
        long usedCount = goodsMapper.countByCategory(category.getName());
        if (usedCount > 0) {
            throw new RuntimeException("删除失败：该分类下仍有 " + usedCount + " 种商品。请先删除或移动这些商品，才能删除分类。");
        }

        categoryMapper.deleteById(id);
    }
    @Override
    public List<Category> getAll() {
        return categoryMapper.findAll();
    }
}
