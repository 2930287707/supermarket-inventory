package com.supermarket.supermarketinventory.vo;

import com.supermarket.supermarketinventory.entity.PurchaseOrder;
import com.supermarket.supermarketinventory.entity.PurchaseOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderDetailVO {
    private PurchaseOrder order;
    private List<PurchaseOrderItem> items;
}
