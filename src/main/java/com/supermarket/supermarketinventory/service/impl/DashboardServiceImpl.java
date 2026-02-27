package com.supermarket.supermarketinventory.service.impl;
import com.supermarket.supermarketinventory.dto.StockRecordDTO;
import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.mapper.GoodsMapper;
import com.supermarket.supermarketinventory.mapper.StockRecordMapper;
import com.supermarket.supermarketinventory.mapper.SupplierMapper;
import com.supermarket.supermarketinventory.service.DashboardService;
import com.supermarket.supermarketinventory.vo.ChartDataVO;
import com.supermarket.supermarketinventory.vo.DashboardStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private StockRecordMapper stockRecordMapper;
    @Autowired
    private SupplierMapper supplierMapper;
    @Override
    public DashboardStatsVO getDashboardStats(){
        // 1. 获取商品总数
        long goodsCount = goodsMapper.countAll();
        // 2. 获取分类数量
        int categoryCount = goodsMapper.countDistinctCategory();
//        获取供应商数量
        long supplierCount = supplierMapper.countAll();
        // 3. 获取今日销量 (如果数据库返回null，说明今天没开张，默认给0L)
        Long todaySales = stockRecordMapper.sumTodayOutStock();
        if (todaySales == null) {
            todaySales = 0L;
        }
        // 4. 获取库存预警数量
        List<Goods> warningList = goodsMapper.selectStockWarning();
        int warningCount = warningList.size();
        //获取分类库存分布（饼图）
        List<ChartDataVO> chartData = goodsMapper.sumStockByCategory();
        //获取最近5条操作记录
        List<StockRecordDTO> recentList = stockRecordMapper.selectRecent();
        // 5. 组装VO对象返回
        return DashboardStatsVO.builder()
                .goodsCount(goodsCount)
                .categoryCount(categoryCount)
                .todaySales(todaySales)
                .warningCount(warningCount)
                .supplierCount(supplierCount)
                .pieChartData(chartData)
                .recentRecords(recentList)
                .build();
    }
}
