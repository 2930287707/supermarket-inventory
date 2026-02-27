package com.supermarket.supermarketinventory.mapper;
import com.supermarket.supermarketinventory.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    int insert(Category category);
    int update(Category category);
    int deleteById(Long id);
    List<Category> findAll();
    Category selectByName(String name);
    Category selectById(Long id);
}
