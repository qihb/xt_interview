package com.xt.interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DepartmentUsersRequireConfirmationException extends RuntimeException {

    private final long accountCount;

    public DepartmentUsersRequireConfirmationException(long accountCount) {
        super("禁用部门将把关联用户改为未分配，请确认");
        this.accountCount = accountCount;
    }

    public long getAccountCount() {
        return accountCount;
    }
}
