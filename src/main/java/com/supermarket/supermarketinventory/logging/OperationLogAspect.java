package com.supermarket.supermarketinventory.logging;

import com.supermarket.supermarketinventory.security.AuthContext;
import com.supermarket.supermarketinventory.security.JwtUserInfo;
import com.supermarket.supermarketinventory.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);
    private final OperationLogService operationLogService;

    public OperationLogAspect(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String username = resolveUsername();
            String role = resolveRole();
            String requestPath = resolveRequestPath();

            log.info("operation='{}', user='{}', role='{}', path='{}', success={}, costMs={}",
                    operationLog.value(), username, role, requestPath, success, duration);

            persistOperationLog(operationLog.value(), username, role, requestPath, success, duration);
        }
    }

    private void persistOperationLog(String operation, String username, String role, String requestPath, boolean success, long duration) {
        try {
            com.supermarket.supermarketinventory.entity.OperationLog entity = new com.supermarket.supermarketinventory.entity.OperationLog();
            entity.setOperation(operation);
            entity.setUsername(username);
            entity.setRole(role);
            entity.setRequestPath(requestPath);
            entity.setSuccess(success ? 1 : 0);
            entity.setCostMs(duration);
            operationLogService.save(entity);
        } catch (Exception e) {
            log.warn("Save operation log failed: {}", e.getMessage());
        }
    }

    private String resolveUsername() {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        if (currentUser == null || currentUser.getUsername() == null) {
            return "anonymous";
        }
        return currentUser.getUsername();
    }

    private String resolveRole() {
        JwtUserInfo currentUser = AuthContext.getCurrentUser();
        if (currentUser == null || currentUser.getRole() == null) {
            return "UNKNOWN";
        }
        return currentUser.getRole();
    }

    private String resolveRequestPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "N/A";
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getMethod() + " " + request.getRequestURI();
    }
}
