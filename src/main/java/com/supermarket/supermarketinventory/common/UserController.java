package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.dto.ChangePasswordDTO;
import com.supermarket.supermarketinventory.dto.LoginRequestDTO;
import com.supermarket.supermarketinventory.dto.UserCreateDTO;
import com.supermarket.supermarketinventory.dto.UserStatusDTO;
import com.supermarket.supermarketinventory.dto.UserUpdateDTO;
import com.supermarket.supermarketinventory.entity.SysUser;
import com.supermarket.supermarketinventory.logging.OperationLog;
import com.supermarket.supermarketinventory.security.AuthContext;
import com.supermarket.supermarketinventory.security.JwtProperties;
import com.supermarket.supermarketinventory.security.JwtTokenUtil;
import com.supermarket.supermarketinventory.security.JwtUserInfo;
import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.UserService;
import com.supermarket.supermarketinventory.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
@RequireRole({"ADMIN", "STAFF", "PURCHASER", "ANALYST"})
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, JwtProperties jwtProperties) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    @OperationLog("User login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        SysUser user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        String token = jwtTokenUtil.generateToken(user);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(jwtProperties.getExpireHours() * 3600);
        loginVO.setUser(user);
        return Result.success(loginVO);
    }

    @PostMapping("/change-password")
    @OperationLog("Change password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO request) {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        if (currentUser != null && !currentUser.getUsername().equals(request.getUsername())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Can only change your own password");
        }
        userService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @GetMapping("/me")
    public Result<JwtUserInfo> currentUser() {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Please login first");
        }
        return Result.success(currentUser);
    }

    @GetMapping("/list")
    @RequireRole({"ADMIN"})
    public Result<List<SysUser>> list() {
        return Result.success(userService.listUsers());
    }

    @PostMapping("/add")
    @RequireRole({"ADMIN"})
    @OperationLog("Create user")
    public Result<Void> add(@Valid @RequestBody UserCreateDTO request) {
        userService.addUser(request);
        return Result.success();
    }

    @PostMapping("/update")
    @RequireRole({"ADMIN"})
    @OperationLog("Update user")
    public Result<Void> update(@Valid @RequestBody UserUpdateDTO request) {
        userService.updateUser(request);
        return Result.success();
    }

    @PostMapping("/status")
    @RequireRole({"ADMIN"})
    @OperationLog("Update user status")
    public Result<Void> updateStatus(@Valid @RequestBody UserStatusDTO request) {
        userService.updateUserStatus(request.getId(), request.getStatus());
        return Result.success();
    }

    @PostMapping("/reset-password/{id}")
    @RequireRole({"ADMIN"})
    @OperationLog("Reset user password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id, "123456");
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @RequireRole({"ADMIN"})
    @OperationLog("Delete user")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
