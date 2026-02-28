package com.supermarket.supermarketinventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderCreateDTO {

    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    private String remark;

    @Valid
    @NotEmpty(message = "采购明细不能为空")
    private List<Item> items;

    @Data
    public static class Item {
        @NotNull(message = "商品不能为空")
        private Long goodsId;

        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量必须大于0")
        private Long qty;

        @NotNull(message = "采购单价不能为空")
        @DecimalMin(value = "0.00", message = "采购单价不能小于0")
        private BigDecimal price;
    }
}
