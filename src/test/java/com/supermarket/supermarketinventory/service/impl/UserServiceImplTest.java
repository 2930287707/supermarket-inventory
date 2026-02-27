package com.supermarket.supermarketinventory.service.impl;

import com.supermarket.supermarketinventory.entity.SysUser;
import com.supermarket.supermarketinventory.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void login_shouldSucceedWithPlainPassword_andUpgradePasswordHash() {
        SysUser user = buildUser(1L, "admin", "123456", 1);
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$newHash");
        when(sysUserMapper.updatePasswordById(1L, "$2a$newHash")).thenReturn(1);

        SysUser result = userService.login("admin", "123456");

        assertEquals("admin", result.getUsername());
        assertNull(result.getPassword());
        verify(sysUserMapper).updatePasswordById(1L, "$2a$newHash");
    }

    @Test
    void login_shouldSucceedWithBcryptPassword() {
        SysUser user = buildUser(1L, "admin", "$2a$hash", 1);
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("123456", "$2a$hash")).thenReturn(true);

        SysUser result = userService.login("admin", "123456");

        assertEquals("admin", result.getUsername());
        assertNull(result.getPassword());
        verify(sysUserMapper, never()).updatePasswordById(anyLong(), anyString());
    }

    @Test
    void login_shouldFailWhenUserNotFound() {
        when(sysUserMapper.selectByUsername("missing")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login("missing", "123456"));

        assertEquals("登录失败：用户名不存在", ex.getMessage());
    }

    @Test
    void login_shouldFailWhenUserDisabled() {
        SysUser user = buildUser(1L, "admin", "123456", 0);
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login("admin", "123456"));

        assertEquals("该账号已被禁用，请联系管理员", ex.getMessage());
    }

    @Test
    void login_shouldFailWhenPasswordIncorrect() {
        SysUser user = buildUser(1L, "admin", "123456", 1);
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login("admin", "wrong-password"));

        assertEquals("登录失败：密码错误", ex.getMessage());
    }

    @Test
    void changePassword_shouldUpdateEncodedPassword() {
        SysUser user = buildUser(2L, "admin", "$2a$hash", 1);
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("old123456", "$2a$hash")).thenReturn(true);
        when(passwordEncoder.encode("new123456")).thenReturn("$2a$newHash");
        when(sysUserMapper.updatePasswordById(2L, "$2a$newHash")).thenReturn(1);

        userService.changePassword("admin", "old123456", "new123456");

        verify(sysUserMapper).updatePasswordById(2L, "$2a$newHash");
    }

    @Test
    void changePassword_shouldFailWhenOldAndNewSame() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword("admin", "same123", "same123"));
        assertEquals("新密码不能与旧密码相同", ex.getMessage());
    }

    @Test
    void changePassword_shouldFailWhenUserNotExist() {
        when(sysUserMapper.selectByUsername("admin")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword("admin", "old123", "new123"));
        assertEquals("用户不存在", ex.getMessage());
    }

    @Test
    void changePassword_shouldFailWhenOldPasswordWrong() {
        SysUser user = buildUser(2L, "admin", "$2a$hash", 1);
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("badOld", "$2a$hash")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword("admin", "badOld", "new123"));
        assertEquals("旧密码不正确", ex.getMessage());
    }

    @Test
    void changePassword_shouldFailWhenDbUpdateAffectsNoRows() {
        SysUser user = buildUser(2L, "admin", "$2a$hash", 1);
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("old123", "$2a$hash")).thenReturn(true);
        when(passwordEncoder.encode("new123")).thenReturn("$2a$newHash");
        when(sysUserMapper.updatePasswordById(2L, "$2a$newHash")).thenReturn(0);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.changePassword("admin", "old123", "new123"));
        assertEquals("密码更新失败，请稍后重试", ex.getMessage());
    }

    private SysUser buildUser(Long id, String username, String password, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setStatus(status);
        return user;
    }
}
