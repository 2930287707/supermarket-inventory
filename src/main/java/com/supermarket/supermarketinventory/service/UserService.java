package com.supermarket.supermarketinventory.service;

import com.supermarket.supermarketinventory.entity.SysUser;

public interface UserService {
    SysUser login(String username, String rawPassword);

    void changePassword(String username, String oldPassword, String newPassword);
}
