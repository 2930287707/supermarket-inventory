package com.supermarket.supermarketinventory.service.impl;
import com.supermarket.supermarketinventory.entity.SysUser;
import com.supermarket.supermarketinventory.mapper.SysUserMapper;
import com.supermarket.supermarketinventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser login(String username, String password) {
        // 1. 根据用户名去查人
        SysUser user = sysUserMapper.selectByUsername(username);

        // 2. 如果查不到，说明用户名输错了
        if (user == null) {
            throw new RuntimeException("登录失败：用户名不存在");
        }

        // 3. 如果查到了，比对密码
        // 注意：实际项目中密码要加密（如MD5），这里为了简化教学，先用明文比对
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("登录失败：密码错误");
        }

        // 4. 如果被禁用了（status = 0）
        if (user.getStatus() == 0) {
            throw new RuntimeException("该账号已被禁用，请联系管理员");
        }

        // 5. 登录成功，把人返回去
        // 为了安全，把密码抹掉，不传给前端
        user.setPassword(null);
        return user;
    }
}
