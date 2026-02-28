package com.supermarket.supermarketinventory.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseOrder {
    private Long id;
    private String orderNo;
    private Long supplierId;
    private String supplierName;
    private Integer status;
    private BigDecimal totalAmount;
    private String remark;
    private String creator;
    private String auditBy;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
