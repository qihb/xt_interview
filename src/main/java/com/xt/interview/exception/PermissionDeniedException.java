package com.xt.interview.exception;

public class PermissionDeniedException extends RuntimeException {

    private final String requiredPermission;

    public PermissionDeniedException(String requiredPermission) {
        super("没有执行该操作的权限");
        this.requiredPermission = requiredPermission;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }
}
