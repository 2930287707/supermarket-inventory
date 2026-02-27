package com.supermarket.supermarketinventory.service.impl;
import com.supermarket.supermarketinventory.entity.Supplier;
import com.supermarket.supermarketinventory.mapper.SupplierMapper;
import com.supermarket.supermarketinventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierMapper supplierMapper;
    @Override
    public void add(Supplier supplier) {
        supplierMapper.insert(supplier);
    }
    @Override
    public void update(Supplier supplier) {
        supplierMapper.update(supplier);
    }
    @Override
    public void delete(Long id) {
        supplierMapper.deleteById(id);
    }
    @Override
    public List<Supplier> getAll() {
        return supplierMapper.findAll();
    }
}
