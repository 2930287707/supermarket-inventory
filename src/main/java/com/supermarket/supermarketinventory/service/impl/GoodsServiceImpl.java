package com.supermarket.supermarketinventory.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supermarket.supermarketinventory.common.PageResult;
import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.mapper.GoodsMapper;
import com.supermarket.supermarketinventory.mapper.StockRecordMapper;
import com.supermarket.supermarketinventory.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    public PageResult<Goods> getGoodsPage(int pageNum, int pageSize, String name, String barcode) {
        // 1. 设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        // 2. 执行查询 (Mapper 的 findPage 支持动态 SQL)
        List<Goods> list = goodsMapper.findPage(name, barcode);
        // 3. 获取分页详情
        PageInfo<Goods> pageInfo = new PageInfo<>(list);
        // 4. 封装成 PageResult 返回
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getList()
        );
    }

    @Override
    public void addGoods(Goods goods) {
        if (goods.getBarcode() == null || goods.getBarcode().isEmpty()) {
            throw new IllegalArgumentException("商品条码不能为空");
        }
        Goods exist = goodsMapper.selectByBarcode(goods.getBarcode());
        if (exist != null) {
            throw new RuntimeException("错误：该条码 [" + goods.getBarcode() + "] 已存在，即使是名字不同也不能重复录入！");
        }
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        if (goods.getStockCurrent() == null) {
            goods.setStockCurrent(0L);
        }
        goodsMapper.insert(goods);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(Goods goods) {
        if (goods.getId() == null) {
            throw new RuntimeException("修改商品时ID不能为空");
        }
        // 严格校验：条码不能和数据库里其他商品的条码重复
        Goods exist = goodsMapper.selectByBarcodeAndNotId(goods.getBarcode(), goods.getId());
        if (exist != null) {
            throw new RuntimeException("修改失败：条码 [" + goods.getBarcode() + "] 已被商品 [" + exist.getName() + "] 占用");
        }
        goods.setUpdateTime(LocalDateTime.now());
        int rows = goodsMapper.update(goods);
        if (rows == 0) {
            throw new RuntimeException("商品不存在，更新失败");
        }
    }

    @Override
    public List<Goods> getAllGoods() {
        // 这里的 findPage 需要传参数，传 null 代表查所有
        return goodsMapper.findPage(null, null);
    }

    @Override
    public Goods getGoodsById(Long id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public List<Goods> getWarningGoods() {
        return goodsMapper.selectStockWarning();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoods(Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (goods.getStockCurrent() > 0) {
            throw new RuntimeException("删除失败,该商品还有库存" + goods.getStockCurrent() + ",请先进行出库或者报损处理。");
        }
        // 检查是否有历史流水
        long recordCount = stockRecordMapper.countByGoodsId(id);
        if (recordCount > 0) {
            throw new RuntimeException("删除失败：该商品存在 " + recordCount + " 条历史出入库记录，为保证财务数据完整，禁止物理删除。");
        }
        goodsMapper.deleteById(id);
    }
}
