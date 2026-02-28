package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.dto.ChangePasswordDTO;
import com.supermarket.supermarketinventory.dto.LoginRequestDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin
@RequireRole({"ADMIN", "MANAGER", "STAFF"})
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
    @OperationLog("用户登录")
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
    @OperationLog("修改密码")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO request) {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        if (currentUser != null && !currentUser.getUsername().equals(request.getUsername())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能修改其他用户的密码");
        }
        userService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @GetMapping("/me")
    public Result<JwtUserInfo> currentUser() {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return Result.success(currentUser);
    }
}
