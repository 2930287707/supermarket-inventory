package com.supermarket.supermarketinventory.security;

import com.supermarket.supermarketinventory.common.BusinessException;
import com.supermarket.supermarketinventory.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        JwtUserInfo userInfo = jwtTokenUtil.parseToken(token);
        AuthContext.setCurrentUser(userInfo);
        checkRolePermission(handlerMethod, userInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return request.getHeader("X-Token");
    }

    private void checkRolePermission(HandlerMethod handlerMethod, JwtUserInfo userInfo) {
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole == null) {
            return;
        }

        if (!StringUtils.hasText(userInfo.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号未分配角色");
        }

        String currentRole = userInfo.getRole();
        boolean hasPermission = Arrays.stream(requireRole.value())
                .anyMatch(allowedRole -> allowedRole.equalsIgnoreCase(currentRole));
        if (!hasPermission) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "当前账号无此操作权限");
        }
    }
}
