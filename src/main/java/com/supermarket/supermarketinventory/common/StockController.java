package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.dto.StockRecordDTO;
import com.supermarket.supermarketinventory.entity.StockRecord;
import com.supermarket.supermarketinventory.logging.OperationLog;
import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.StockService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock")
@CrossOrigin
@RequireRole({"ADMIN", "MANAGER", "STAFF"})
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/operate")
    @OperationLog("库存出入库")
    public Result<Void> operate(@RequestBody StockRecord stockRecord) {
        stockService.addStockRecord(stockRecord);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<StockRecordDTO>> list() {
        return Result.success(stockService.getStockRecords());
    }
}
