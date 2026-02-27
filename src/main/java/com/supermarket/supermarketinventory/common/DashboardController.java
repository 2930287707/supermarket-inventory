package com.supermarket.supermarketinventory.common;
import com.supermarket.supermarketinventory.service.DashboardService;
import com.supermarket.supermarketinventory.vo.DashboardStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        DashboardStatsVO stats = dashboardService.getDashboardStats();
        return Result.success(stats);
    }
}
