package com.example.productservice.common;

import com.example.productservice.domain.enums.UserRole;

import java.util.Arrays;

public final class RoleGuard {
    private RoleGuard() {
    }

    public static void requireRole(String rawRole, UserRole... allowRoles) {
        if (rawRole == null || rawRole.isBlank()) {
            throw new BizException("缺少请求头 X-Role");
        }
        UserRole current;
        try {
            current = UserRole.valueOf(rawRole.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BizException("非法角色: " + rawRole);
        }
        boolean matched = Arrays.stream(allowRoles).anyMatch(role -> role == current);
        if (!matched) {
            throw new BizException("权限不足，当前角色: " + current);
        }
    }
}
