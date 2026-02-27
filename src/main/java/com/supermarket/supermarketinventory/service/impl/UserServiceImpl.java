package com.supermarket.supermarketinventory.service.impl;

import com.supermarket.supermarketinventory.entity.SysUser;
import com.supermarket.supermarketinventory.mapper.SysUserMapper;
import com.supermarket.supermarketinventory.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SysUser login(String username, String rawPassword) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("登录失败：用户名不存在");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("该账号已被禁用，请联系管理员");
        }

        if (!passwordMatches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("登录失败：密码错误");
        }

        if (shouldUpgradeToBcrypt(user.getPassword())) {
            String encoded = passwordEncoder.encode(rawPassword);
            sysUserMapper.updatePasswordById(user.getId(), encoded);
        }

        user.setPassword(null);
        return user;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }

        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!passwordMatches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        int rows = sysUserMapper.updatePasswordById(user.getId(), encodedPassword);
        if (rows == 0) {
            throw new IllegalStateException("密码更新失败，请稍后重试");
        }
    }

    private boolean shouldUpgradeToBcrypt(String storedPassword) {
        return storedPassword != null
                && !storedPassword.startsWith("$2a$")
                && !storedPassword.startsWith("$2b$")
                && !storedPassword.startsWith("$2y$");
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null || rawPassword == null) {
            return false;
        }

        if (!shouldUpgradeToBcrypt(storedPassword)) {
            try {
                return passwordEncoder.matches(rawPassword, storedPassword);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }

        return storedPassword.equals(rawPassword);
    }
}
