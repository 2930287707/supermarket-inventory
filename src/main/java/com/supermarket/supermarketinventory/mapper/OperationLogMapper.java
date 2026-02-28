package com.supermarket.supermarketinventory.mapper;

import com.supermarket.supermarketinventory.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    int insert(OperationLog operationLog);

    List<OperationLog> selectPage(
            @Param("username") String username,
            @Param("operation") String operation,
            @Param("success") Integer success
    );
}
