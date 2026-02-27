package com.supermarket.supermarketinventory.common;
import com.supermarket.supermarketinventory.dto.StockRecordDTO;
import com.supermarket.supermarketinventory.entity.StockRecord;
import com.supermarket.supermarketinventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/stock")
@CrossOrigin
public class StockController {
    @Autowired
    private StockService stockService;
    @PostMapping("/operate")
    public Result<Void>operate(@RequestBody StockRecord stockRecord){
        try{
            stockService.addStockRecord(stockRecord);
            return Result.success();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
    @GetMapping("/list")
    public Result<List<StockRecordDTO>> list() {
        return Result.success(stockService.getStockRecords());
    }
}

