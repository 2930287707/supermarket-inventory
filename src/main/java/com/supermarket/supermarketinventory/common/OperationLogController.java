package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.entity.OperationLog;
import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.OperationLogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operation-log")
@CrossOrigin
@RequireRole({"ADMIN", "MANAGER"})
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping("/list")
    public Result<PageResult<OperationLog>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Integer success
    ) {
        PageResult<OperationLog> page = operationLogService.page(pageNum, pageSize, username, operation, success);
        return Result.success(page);
    }
}
