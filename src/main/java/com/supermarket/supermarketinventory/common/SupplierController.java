package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.entity.Supplier;
import com.supermarket.supermarketinventory.logging.OperationLog;
import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.SupplierService;
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
@RequestMapping("/supplier")
@CrossOrigin
@RequireRole({"ADMIN", "MANAGER", "STAFF"})
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/list")
    public Result<List<Supplier>> list() {
        return Result.success(supplierService.getAll());
    }

    @PostMapping("/add")
    @RequireRole({"ADMIN", "MANAGER"})
    @OperationLog("新增供应商")
    public Result<Void> add(@RequestBody Supplier supplier) {
        supplierService.add(supplier);
        return Result.success();
    }

    @PostMapping("/update")
    @RequireRole({"ADMIN", "MANAGER"})
    @OperationLog("修改供应商")
    public Result<Void> update(@RequestBody Supplier supplier) {
        supplierService.update(supplier);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @RequireRole({"ADMIN"})
    @OperationLog("删除供应商")
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return Result.success();
    }
}
