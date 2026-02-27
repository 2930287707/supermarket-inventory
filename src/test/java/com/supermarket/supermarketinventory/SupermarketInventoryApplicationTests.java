package com.supermarket.supermarketinventory;

import com.supermarket.supermarketinventory.entity.Goods;
import com.supermarket.supermarketinventory.mapper.GoodsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class GoodsMapperTest {


    @Autowired
    private com.supermarket.supermarketinventory.service.GoodsService goodsService; // 注入 Service

    @Test
    void testServiceAddGoods() {
        System.out.println("====== 测试 Service 业务逻辑 ======");

        // 1. 创建一个新商品
        Goods g1 = new Goods();
        g1.setBarcode("999999"); // 随便编一个
        g1.setName("Service测试商品");
        g1.setPriceIn(new BigDecimal("10.00"));
        g1.setPriceOut(new BigDecimal("15.00"));

        // 2. 第一次添加，应该成功
        try {
            goodsService.addGoods(g1);
            System.out.println("✅ 第一次添加成功！");
        } catch (Exception e) {
            System.out.println("❌ 第一次添加失败：" + e.getMessage());
        }

        // 3. 第二次添加同样的条码，应该报错（这是我们要的效果！）
        System.out.println("------ 尝试添加重复条码 ------");
        try {
            goodsService.addGoods(g1); // 注意：这里还是存 g1，条码一样的
            System.out.println("❌ 失败：系统没有拦截住重复条码！");
        } catch (RuntimeException e) {
            System.out.println("✅ 成功拦截：系统抛出了预期的异常 -> " + e.getMessage());
        }
    }

}
