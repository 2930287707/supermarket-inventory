package com.supermarket.supermarketinventory.mapper;

import com.supermarket.supermarketinventory.entity.PurchaseOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderItemMapper {
    int insertBatch(@Param("items") List<PurchaseOrderItem> items);

    List<PurchaseOrderItem> selectByOrderId(Long orderId);
}
