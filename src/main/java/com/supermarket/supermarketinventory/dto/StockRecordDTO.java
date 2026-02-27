package com.supermarket.supermarketinventory.dto;
import com.supermarket.supermarketinventory.entity.StockRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockRecordDTO extends StockRecord {
    private String goodsName;
    private String barcode;
}
