package com.supermarket.supermarketinventory.common;
import com.supermarket.supermarketinventory.entity.Supplier;
import com.supermarket.supermarketinventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/supplier")
@CrossOrigin
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @GetMapping("/list")
    public Result<List<Supplier>> list(){
        List<Supplier> list = supplierService.getAll();
        return Result.success(list);
    }
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Supplier supplier) {
        supplierService.add(supplier);
        return Result.success();
    }
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Supplier supplier) {
        supplierService.update(supplier);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return Result.success();
    }
}
