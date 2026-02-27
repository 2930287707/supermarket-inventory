package com.supermarket.supermarketinventory.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 作用：拦截所有 Controller 层抛出的异常，统一封装成 Result 对象返回给前端
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 拦截所有 RuntimeException (包括我们自己抛出的 throw new RuntimeException("..."))
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage()); // 直接把错误信息传给前端
    }

    /**
     * 拦截所有未知的 Exception (比如空指针、数据库连接失败等系统级错误)
     * 系统级错误不应该把详细堆栈直接抛给前端，显示“系统繁忙”即可，但后台要记录日志
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e); // 记录完整堆栈用于排查
        return Result.error("系统繁忙，请联系管理员");
    }
}
