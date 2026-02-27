package com.supermarket.supermarketinventory.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Supplier {
    private Long id;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
