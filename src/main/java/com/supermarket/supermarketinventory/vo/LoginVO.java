package com.supermarket.supermarketinventory.vo;

import com.supermarket.supermarketinventory.entity.SysUser;
import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private String tokenType;
    private long expiresIn;
    private SysUser user;
}
