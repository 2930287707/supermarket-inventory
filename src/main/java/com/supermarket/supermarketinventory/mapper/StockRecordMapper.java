package com.supermarket.supermarketinventory.mapper;
import com.supermarket.supermarketinventory.dto.StockRecordDTO;
import com.supermarket.supermarketinventory.entity.StockRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StockRecordMapper {
    int insert(StockRecord record);
    List<StockRecordDTO> selectAllWithGoods();
    // 统计今日出库总数（今日销售量）
    Long sumTodayOutStock();
    long countByGoodsId(Long goodsId);
    List<StockRecordDTO> selectRecent();
}
