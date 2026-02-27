package com.supermarket.supermarketinventory.entity;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Goods {
    private Long id;
    private String barcode;
    private String name;
    private String category;
    private String spec;
    private BigDecimal priceIn;
    private BigDecimal priceOut;
    private Long stockCurrent;
    private Long stockWarning;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
