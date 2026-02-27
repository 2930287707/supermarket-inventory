package com.supermarket.supermarketinventory.mapper;
import com.supermarket.supermarketinventory.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper {
    SysUser selectByUsername(String username);
}
