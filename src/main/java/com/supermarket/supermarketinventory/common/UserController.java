package com.supermarket.supermarketinventory.common;
import com.supermarket.supermarketinventory.entity.SysUser;
import com.supermarket.supermarketinventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody SysUser userParam) {
        try {
            // 参数校验
            if (userParam.getUsername() == null || userParam.getPassword() == null) {
                return Result.error("用户名或密码不能为空");
            }
            // 调用业务逻辑
            SysUser user = userService.login(userParam.getUsername(), userParam.getPassword());
            // 返回成功数据（包含用户ID、名字、角色）
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
