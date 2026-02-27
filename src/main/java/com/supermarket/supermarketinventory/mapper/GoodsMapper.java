package com.supermarket.supermarketinventory.mapper;
import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.vo.ChartDataVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GoodsMapper {
    int insert(Goods goods);
    int deleteById(Long id);
    int update(Goods goods);
    Goods selectById(Long id);
    Goods selectByBarcode(String barcode);
    List<Goods> findPage(String name, String barcode);
    int updateStock(Long id, int change);
    List<Goods> selectStockWarning();
    long countAll();
    int countDistinctCategory();
    long countByCategory(String categoryName);
    Goods selectByBarcodeAndNotId(String barcode, Long id);
    int updateCategoryName(String oldName, String newName);
    List<ChartDataVO> sumStockByCategory();
}
