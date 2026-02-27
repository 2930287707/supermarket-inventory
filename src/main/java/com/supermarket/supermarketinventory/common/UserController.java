package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.dto.ChangePasswordDTO;
import com.supermarket.supermarketinventory.dto.LoginRequestDTO;
import com.supermarket.supermarketinventory.entity.SysUser;
import com.supermarket.supermarketinventory.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<SysUser> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        SysUser user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return Result.success(user);
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO request) {
        userService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }
}
