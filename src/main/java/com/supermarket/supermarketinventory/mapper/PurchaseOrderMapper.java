package com.supermarket.supermarketinventory.mapper;

import com.supermarket.supermarketinventory.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PurchaseOrderMapper {
    int insert(PurchaseOrder purchaseOrder);

    PurchaseOrder selectById(Long id);

    List<PurchaseOrder> selectPage(
            @Param("orderNo") String orderNo,
            @Param("status") Integer status,
            @Param("supplierId") Long supplierId
    );

    int markApproved(
            @Param("id") Long id,
            @Param("auditBy") String auditBy,
            @Param("auditTime") LocalDateTime auditTime
    );
}
