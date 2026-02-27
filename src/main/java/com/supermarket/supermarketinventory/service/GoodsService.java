package com.supermarket.supermarketinventory.service;
import com.supermarket.supermarketinventory.common.PageResult;
import com.supermarket.supermarketinventory.entity.Goods;
import java.util.List;

public interface GoodsService {
    void addGoods(Goods goods);
    void updateGoods(Goods goods);
    List<Goods> getAllGoods();
    Goods getGoodsById(Long id);
    PageResult<Goods> getGoodsPage(int pageNum, int pageSize, String name, String barcode);
    List<Goods> getWarningGoods();
    void deleteGoods(Long id);
}
