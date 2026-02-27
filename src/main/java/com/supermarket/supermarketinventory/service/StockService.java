package com.supermarket.supermarketinventory.service;
import com.supermarket.supermarketinventory.dto.StockRecordDTO;
import com.supermarket.supermarketinventory.entity.StockRecord;
import java.util.List;

public interface StockService {
    void addStockRecord(StockRecord stockRecord);
    List<StockRecordDTO> getStockRecords();
}
