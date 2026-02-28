package com.supermarket.supermarketinventory.service;

import com.supermarket.supermarketinventory.common.PageResult;
import com.supermarket.supermarketinventory.dto.PurchaseOrderCreateDTO;
import com.supermarket.supermarketinventory.entity.PurchaseOrder;
import com.supermarket.supermarketinventory.vo.PurchaseOrderDetailVO;

public interface PurchaseOrderService {
    Long create(PurchaseOrderCreateDTO request, String creator);

    PageResult<PurchaseOrder> page(int pageNum, int pageSize, String orderNo, Integer status, Long supplierId);

    PurchaseOrderDetailVO detail(Long id);

    void approveAndStockIn(Long id, Long operatorId, String auditor);
}
