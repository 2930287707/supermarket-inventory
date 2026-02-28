package com.supermarket.supermarketinventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50 位")
    private String nickname;

    @NotBlank(message = "角色不能为空")
    private String role;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
