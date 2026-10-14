package com.xt.interview.util;

import com.xt.interview.exception.PermissionDeniedException;
import jakarta.servlet.http.HttpServletRequest;

public final class RolePermission {

    private RolePermission() {
    }

    public static void requireAtLeast(HttpServletRequest request, int requiredRoleLevel) {
        Object roleLevel = request.getAttribute("roleLevel");
        if (!(roleLevel instanceof Number)
                || ((Number) roleLevel).intValue() < requiredRoleLevel) {
            String requiredPermission = requiredRoleLevel >= 3
                    ? "需要超级管理员权限"
                    : "需要管理员权限";
            throw new PermissionDeniedException(requiredPermission);
        }
    }
}
