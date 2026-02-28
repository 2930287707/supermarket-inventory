package com.supermarket.supermarketinventory.mapper;

import com.supermarket.supermarketinventory.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SupplierMapper {
    int insert(Supplier supplier);

    int update(Supplier supplier);

    int deleteById(Long id);

    Supplier selectById(Long id);

    List<Supplier> findAll();

    long countAll();
}
