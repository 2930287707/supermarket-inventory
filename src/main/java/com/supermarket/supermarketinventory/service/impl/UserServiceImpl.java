package com.supermarket.supermarketinventory.service.impl;

import com.supermarket.supermarketinventory.dto.UserCreateDTO;
import com.supermarket.supermarketinventory.dto.UserUpdateDTO;
import com.supermarket.supermarketinventory.entity.SysUser;
import com.supermarket.supermarketinventory.mapper.SysUserMapper;
import com.supermarket.supermarketinventory.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "STAFF", "PURCHASER", "ANALYST");

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
            throw new IllegalArgumentException("Login failed: username does not exist");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("Account is disabled");
        }

        if (!passwordMatches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Login failed: password is incorrect");
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
            throw new IllegalArgumentException("New password cannot be the same as old password");
        }

        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        if (!passwordMatches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        int rows = sysUserMapper.updatePasswordById(user.getId(), encodedPassword);
        if (rows == 0) {
            throw new IllegalStateException("Failed to update password");
        }
    }

    @Override
    public List<SysUser> listUsers() {
        return sysUserMapper.selectAll();
    }

    @Override
    public void addUser(UserCreateDTO request) {
        String username = request.getUsername().trim();
        if (sysUserMapper.selectByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        String role = normalizeAndValidateRole(request.getRole());
        Integer status = normalizeAndValidateStatus(request.getStatus());

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setNickname(request.getNickname().trim());
        user.setRole(role);
        user.setStatus(status);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        int rows = sysUserMapper.insert(user);
        if (rows == 0) {
            throw new IllegalStateException("Failed to create user");
        }
    }

    @Override
    public void updateUser(UserUpdateDTO request) {
        SysUser existing = sysUserMapper.selectById(request.getId());
        if (existing == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (isRootAdmin(existing)) {
            throw new IllegalArgumentException("Built-in admin account cannot be modified");
        }

        SysUser user = new SysUser();
        user.setId(request.getId());
        user.setNickname(request.getNickname().trim());
        user.setRole(normalizeAndValidateRole(request.getRole()));
        user.setStatus(normalizeAndValidateStatus(request.getStatus()));
        int rows = sysUserMapper.updateUser(user);
        if (rows == 0) {
            throw new IllegalStateException("Failed to update user");
        }
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        Integer normalizedStatus = normalizeAndValidateStatus(status);
        if (isRootAdmin(existing) && normalizedStatus == 0) {
            throw new IllegalArgumentException("Built-in admin account cannot be disabled");
        }

        int rows = sysUserMapper.updateStatusById(id, normalizedStatus);
        if (rows == 0) {
            throw new IllegalStateException("Failed to update user status");
        }
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        int rows = sysUserMapper.updatePasswordById(id, passwordEncoder.encode(newPassword));
        if (rows == 0) {
            throw new IllegalStateException("Failed to reset password");
        }
    }

    @Override
    public void deleteUser(Long id) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (isRootAdmin(existing)) {
            throw new IllegalArgumentException("Built-in admin account cannot be deleted");
        }

        int rows = sysUserMapper.deleteById(id);
        if (rows == 0) {
            throw new IllegalStateException("Failed to delete user");
        }
    }

    private boolean isRootAdmin(SysUser user) {
        return user.getUsername() != null && "admin".equalsIgnoreCase(user.getUsername());
    }

    private String normalizeAndValidateRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        // Backward compatibility for legacy role value.
        if ("MANAGER".equals(normalized)) {
            normalized = "ADMIN";
        }
        if (!ALLOWED_ROLES.contains(normalized)) {
            throw new IllegalArgumentException("Invalid role, only ADMIN/STAFF/PURCHASER/ANALYST are allowed");
        }
        return normalized;
    }

    private Integer normalizeAndValidateStatus(Integer status) {
        Integer normalized = status == null ? 1 : status;
        if (normalized != 0 && normalized != 1) {
            throw new IllegalArgumentException("Invalid status, only 0 or 1 is allowed");
        }
        return normalized;
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
