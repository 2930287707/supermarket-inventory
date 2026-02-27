package com.supermarket.supermarketinventory.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockRecord {
    private Long id;
    private Long goodId;
    private Long operatorId;
    private Integer type;
    private Long qty;
    private String remark;
    private LocalDateTime dateTime;
}
