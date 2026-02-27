package com.supermarket.supermarketinventory.mapper;

import com.supermarket.supermarketinventory.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {
    SysUser selectByUsername(String username);

    int updatePasswordById(@Param("id") Long id, @Param("password") String password);
}
