ALTER TABLE `sys_account`
    ADD INDEX `idx_account_department_deleted` (`department_id`, `deleted`);
