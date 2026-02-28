package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.DashboardService;
import com.supermarket.supermarketinventory.vo.DashboardStatsVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
@RequireRole({"ADMIN", "STAFF", "PURCHASER", "ANALYST"})
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        DashboardStatsVO stats = dashboardService.getDashboardStats();
        return Result.success(stats);
    }
}
