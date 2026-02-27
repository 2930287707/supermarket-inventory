package com.supermarket.supermarketinventory.service;
import com.supermarket.supermarketinventory.entity.Supplier;
import java.util.List;

public interface SupplierService {
    void add(Supplier supplier);
    void update(Supplier supplier);
    void delete(Long id);
    List<Supplier> getAll();
}
