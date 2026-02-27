package com.supermarket.supermarketinventory.entity;
import lombok.Data;

@Data
public class SysUser {
    private long id;
    private String username;
    private String password;
    private String nickname;
    private String role;
    private Integer status;
}
