package com.supermarket.supermarketinventory.vo;
import com.supermarket.supermarketinventory.dto.StockRecordDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO {
    private Long goodsCount;      // 商品总数
    private Integer categoryCount;// 分类数量
    private Long todaySales;      // 今日销量
    private Integer warningCount; // 库存预警数
    private Long supplierCount;
    private List<ChartDataVO> pieChartData;//图表数据
    private List<StockRecordDTO> recentRecords;//最近活动记录
}
