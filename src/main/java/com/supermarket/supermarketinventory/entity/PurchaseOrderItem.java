package com.supermarket.supermarketinventory.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseOrderItem {
    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private Long qty;
    private BigDecimal price;
    private BigDecimal amount;
    private LocalDateTime createTime;
}
