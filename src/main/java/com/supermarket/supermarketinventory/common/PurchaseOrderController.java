package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.dto.PurchaseOrderCreateDTO;
import com.supermarket.supermarketinventory.entity.PurchaseOrder;
import com.supermarket.supermarketinventory.logging.OperationLog;
import com.supermarket.supermarketinventory.security.AuthContext;
import com.supermarket.supermarketinventory.security.JwtUserInfo;
import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.PurchaseOrderService;
import com.supermarket.supermarketinventory.vo.PurchaseOrderDetailVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase")
@CrossOrigin
@RequireRole({"ADMIN", "STAFF", "PURCHASER"})
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping("/create")
    @RequireRole({"ADMIN", "PURCHASER"})
    @OperationLog("Create purchase order")
    public Result<Long> create(@Valid @RequestBody PurchaseOrderCreateDTO request) {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        String creator = currentUser == null ? "system" : currentUser.getUsername();
        Long id = purchaseOrderService.create(request, creator);
        return Result.success(id);
    }

    @GetMapping("/list")
    public Result<PageResult<PurchaseOrder>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long supplierId
    ) {
        PageResult<PurchaseOrder> page = purchaseOrderService.page(pageNum, pageSize, orderNo, status, supplierId);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<PurchaseOrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(purchaseOrderService.detail(id));
    }

    @PostMapping("/approve/{id}")
    @RequireRole({"ADMIN", "PURCHASER"})
    @OperationLog("Approve purchase order")
    public Result<Void> approve(@PathVariable Long id) {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        Long operatorId = currentUser == null ? 0L : currentUser.getUserId();
        String auditor = currentUser == null ? "system" : currentUser.getUsername();
        purchaseOrderService.approveAndStockIn(id, operatorId, auditor);
        return Result.success();
    }
}