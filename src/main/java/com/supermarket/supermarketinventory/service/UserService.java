package com.supermarket.supermarketinventory.service;

import com.supermarket.supermarketinventory.dto.UserCreateDTO;
import com.supermarket.supermarketinventory.dto.UserUpdateDTO;
import com.supermarket.supermarketinventory.entity.SysUser;

import java.util.List;

public interface UserService {
    SysUser login(String username, String rawPassword);

    void changePassword(String username, String oldPassword, String newPassword);

    List<SysUser> listUsers();

    void addUser(UserCreateDTO request);

    void updateUser(UserUpdateDTO request);

    void updateUserStatus(Long id, Integer status);

    void resetPassword(Long id, String newPassword);

    void deleteUser(Long id);
}
