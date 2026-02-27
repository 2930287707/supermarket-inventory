package com.supermarket.supermarketinventory.vo;
import lombok.Data;

@Data
public class ChartDataVO {
    private String name;  // 分类名
    private Long value;   // 库存总数
}
