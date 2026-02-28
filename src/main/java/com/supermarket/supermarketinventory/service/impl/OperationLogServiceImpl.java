package com.supermarket.supermarketinventory.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supermarket.supermarketinventory.common.PageResult;
import com.supermarket.supermarketinventory.entity.OperationLog;
import com.supermarket.supermarketinventory.mapper.OperationLogMapper;
import com.supermarket.supermarketinventory.service.OperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public void save(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }

    @Override
    public PageResult<OperationLog> page(int pageNum, int pageSize, String username, String operation, Integer success) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> list = operationLogMapper.selectPage(username, operation, success);
        PageInfo<OperationLog> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getList()
        );
    }
}
