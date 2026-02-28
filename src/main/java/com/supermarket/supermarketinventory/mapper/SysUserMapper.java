package com.supermarket.supermarketinventory.mapper;

import com.supermarket.supermarketinventory.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {
    SysUser selectByUsername(String username);

    SysUser selectById(Long id);

    List<SysUser> selectAll();

    int insert(SysUser user);

    int updateUser(SysUser user);

    int updateStatusById(@Param("id") Long id, @Param("status") Integer status);

    int updatePasswordById(@Param("id") Long id, @Param("password") String password);

    int deleteById(@Param("id") Long id);
}
