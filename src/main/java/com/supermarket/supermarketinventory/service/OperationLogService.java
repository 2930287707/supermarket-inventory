package com.supermarket.supermarketinventory.service;

import com.supermarket.supermarketinventory.common.PageResult;
import com.supermarket.supermarketinventory.entity.OperationLog;

public interface OperationLogService {

    void save(OperationLog operationLog);

    PageResult<OperationLog> page(int pageNum, int pageSize, String username, String operation, Integer success);
}
