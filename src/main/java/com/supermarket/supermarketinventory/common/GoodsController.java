package com.supermarket.supermarketinventory.common; // 注意：通常 Controller 在 controller 包下，你这里是在 common 包？如果在 controller 包请自行修改 package

import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/goods")
@CrossOrigin
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    // 新增商品
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Goods goods) {
        // 不需要 try-catch，Service 报错会自动被全局异常处理器捕获并返回 Result.error
        goodsService.addGoods(goods);
        return Result.success();
    }

    // 分页查询列表
    @GetMapping("/list")
    public Result<PageResult<Goods>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode
    ) {
        // 调用 Service 的分页方法
        PageResult<Goods> pageResult = goodsService.getGoodsPage(pageNum, pageSize, name, barcode);
        return Result.success(pageResult);
    }

    // 库存预警列表 (这个量通常不大，可以暂时不分页，或者你也可以给它加上分页)
    @GetMapping("/warning-list")
    public Result<List<Goods>> warningList() {
        List<Goods> list = goodsService.getWarningGoods();
        return Result.success(list);
    }

    // 更新商品
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Goods goods) {
        goodsService.updateGoods(goods);
        return Result.success();
    }

    // 删除商品
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        goodsService.deleteGoods(id);
        return Result.success();
    }
}
