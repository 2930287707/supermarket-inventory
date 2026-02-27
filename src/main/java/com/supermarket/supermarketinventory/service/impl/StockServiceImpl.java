package com.supermarket.supermarketinventory.service.impl;
import com.supermarket.supermarketinventory.dto.StockRecordDTO;
import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.entity.StockRecord;
import com.supermarket.supermarketinventory.mapper.GoodsMapper;
import com.supermarket.supermarketinventory.mapper.StockRecordMapper;
import com.supermarket.supermarketinventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    private StockRecordMapper stockRecordMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStockRecord(StockRecord record){
        if (record.getGoodId() == null || record.getQty() == null || record.getType() == null) {
            throw new RuntimeException("参数不完整：商品ID、数量、类型必填");
        }
        // 2. 检查商品是否存在
        Goods goods = goodsMapper.selectById(record.getGoodId());
        if (goods == null) {
            throw new RuntimeException("商品不存在，无法操作库存");
        }
        // 3. 计算库存变动方向
        // type=1 是入库(正数)，type=2 是出库(负数)
        int changeQty = record.getQty().intValue();

        if (record.getType() == 2) {
            // 如果是出库
            if (goods.getStockCurrent() < changeQty) {
                throw new RuntimeException("库存不足！当前库存：" + goods.getStockCurrent());
            }
            changeQty = -changeQty; // 变成负数
        }
        // 4. 第一步数据库操作：插入流水记录
        stockRecordMapper.insert(record);
        // 5. 第二步数据库操作：更新商品总库存
        int updateCount = goodsMapper.updateStock(record.getGoodId(), changeQty);

        if (updateCount == 0) {
            throw new RuntimeException("库存更新失败，请重试");
        }
    }
    @Override
    public List<StockRecordDTO> getStockRecords() {
        return stockRecordMapper.selectAllWithGoods();
    }
}
