package com.supermarket.supermarketinventory.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private String username;
    private String role;
    private String operation;
    private String requestPath;
    private Integer success;
    private Long costMs;
    private LocalDateTime createTime;
}
