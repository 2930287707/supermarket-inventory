package com.supermarket.supermarketinventory.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supermarket.supermarketinventory.common.BusinessException;
import com.supermarket.supermarketinventory.common.ErrorCode;
import com.supermarket.supermarketinventory.common.PageResult;
import com.supermarket.supermarketinventory.dto.PurchaseOrderCreateDTO;
import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.entity.PurchaseOrder;
import com.supermarket.supermarketinventory.entity.PurchaseOrderItem;
import com.supermarket.supermarketinventory.entity.StockRecord;
import com.supermarket.supermarketinventory.entity.Supplier;
import com.supermarket.supermarketinventory.mapper.GoodsMapper;
import com.supermarket.supermarketinventory.mapper.PurchaseOrderItemMapper;
import com.supermarket.supermarketinventory.mapper.PurchaseOrderMapper;
import com.supermarket.supermarketinventory.mapper.StockRecordMapper;
import com.supermarket.supermarketinventory.mapper.SupplierMapper;
import com.supermarket.supermarketinventory.service.PurchaseOrderService;
import com.supermarket.supermarketinventory.vo.PurchaseOrderDetailVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private static final DateTimeFormatter ORDER_NO_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final SupplierMapper supplierMapper;
    private final GoodsMapper goodsMapper;
    private final StockRecordMapper stockRecordMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderMapper purchaseOrderMapper,
                                    PurchaseOrderItemMapper purchaseOrderItemMapper,
                                    SupplierMapper supplierMapper,
                                    GoodsMapper goodsMapper,
                                    StockRecordMapper stockRecordMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderItemMapper = purchaseOrderItemMapper;
        this.supplierMapper = supplierMapper;
        this.goodsMapper = goodsMapper;
        this.stockRecordMapper = stockRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PurchaseOrderCreateDTO request, String creator) {
        Supplier supplier = supplierMapper.selectById(request.getSupplierId());
        if (supplier == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "供应商不存在");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "采购明细不能为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>(request.getItems().size());

        for (PurchaseOrderCreateDTO.Item item : request.getItems()) {
            Goods goods = goodsMapper.selectById(item.getGoodsId());
            if (goods == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "商品不存在，ID=" + item.getGoodsId());
            }
            if (item.getQty() == null || item.getQty() <= 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "采购数量必须大于0");
            }
            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "采购单价不能小于0");
            }

            PurchaseOrderItem orderItem = new PurchaseOrderItem();
            orderItem.setGoodsId(item.getGoodsId());
            orderItem.setQty(item.getQty());
            orderItem.setPrice(item.getPrice());
            orderItem.setAmount(item.getPrice().multiply(BigDecimal.valueOf(item.getQty())));
            totalAmount = totalAmount.add(orderItem.getAmount());
            items.add(orderItem);
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setSupplierId(request.getSupplierId());
        order.setStatus(0);
        order.setTotalAmount(totalAmount);
        order.setRemark(request.getRemark());
        order.setCreator(creator == null ? "system" : creator);
        purchaseOrderMapper.insert(order);

        for (PurchaseOrderItem item : items) {
            item.setOrderId(order.getId());
        }
        purchaseOrderItemMapper.insertBatch(items);
        return order.getId();
    }

    @Override
    public PageResult<PurchaseOrder> page(int pageNum, int pageSize, String orderNo, Integer status, Long supplierId) {
        PageHelper.startPage(pageNum, pageSize);
        List<PurchaseOrder> list = purchaseOrderMapper.selectPage(orderNo, status, supplierId);
        PageInfo<PurchaseOrder> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getList()
        );
    }

    @Override
    public PurchaseOrderDetailVO detail(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购单不存在");
        }
        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(id);
        PurchaseOrderDetailVO detailVO = new PurchaseOrderDetailVO();
        detailVO.setOrder(order);
        detailVO.setItems(items);
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveAndStockIn(Long id, Long operatorId, String auditor) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只有待审核采购单可执行入库");
        }

        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(id);
        if (items == null || items.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "采购单明细为空，无法入库");
        }

        for (PurchaseOrderItem item : items) {
            int change = safeToInt(item.getQty());
            int rows = goodsMapper.updateStock(item.getGoodsId(), change);
            if (rows == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "商品不存在或库存更新失败，商品ID=" + item.getGoodsId());
            }

            StockRecord stockRecord = new StockRecord();
            stockRecord.setGoodId(item.getGoodsId());
            stockRecord.setOperatorId(operatorId == null ? 0L : operatorId);
            stockRecord.setType(1);
            stockRecord.setQty(item.getQty());
            stockRecord.setRemark("采购入库，单号：" + order.getOrderNo());
            stockRecordMapper.insert(stockRecord);
        }

        int updated = purchaseOrderMapper.markApproved(id, auditor, LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "采购单状态已变化，请刷新后重试");
        }
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(ORDER_NO_TIME_FORMAT);
        int suffix = ThreadLocalRandom.current().nextInt(100, 1000);
        return "PO" + timestamp + suffix;
    }

    private int safeToInt(Long value) {
        if (value == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "数量不能为空");
        }
        if (value > Integer.MAX_VALUE) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "数量超过系统上限");
        }
        return value.intValue();
    }
}
