package com.supermarket.supermarketinventory.common;
import lombok.Data;

@Data
public class Result<T> {
    private int code;      // 200成功，其他失败
    private String msg;    // 提示信息
    private T data;        // 返回的数据（泛型）
    // 快捷方法：成功时调用
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "操作成功";
        result.data = data;
        return result;
    }
    // 快捷方法：成功但没数据
    public static <T> Result<T> success() {
        return success(null);
    }
    // 快捷方法：失败时调用
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 500; // 简单起见，错误统一叫500
        result.msg = msg;
        return result;
    }
}
