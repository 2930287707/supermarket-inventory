package com.supermarket.supermarketinventory.common;

import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.logging.OperationLog;
import com.supermarket.supermarketinventory.security.RequireRole;
import com.supermarket.supermarketinventory.service.GoodsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
@CrossOrigin
@RequireRole({"ADMIN", "STAFF", "PURCHASER"})
public class GoodsController {

    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("/add")
    @RequireRole({"ADMIN"})
    @OperationLog("新增商品")
    public Result<Void> add(@RequestBody Goods goods) {
        goodsService.addGoods(goods);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResult<Goods>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode
    ) {
        PageResult<Goods> pageResult = goodsService.getGoodsPage(pageNum, pageSize, name, barcode);
        return Result.success(pageResult);
    }

    @GetMapping("/warning-list")
    public Result<List<Goods>> warningList() {
        return Result.success(goodsService.getWarningGoods());
    }

    @PostMapping("/update")
    @RequireRole({"ADMIN"})
    @OperationLog("修改商品")
    public Result<Void> update(@RequestBody Goods goods) {
        goodsService.updateGoods(goods);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @RequireRole({"ADMIN"})
    @OperationLog("删除商品")
    public Result<Void> delete(@PathVariable Long id) {
        goodsService.deleteGoods(id);
        return Result.success();
    }
}
