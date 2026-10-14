-- ==================== 账号用户名改为大小写敏感 ====================
ALTER TABLE `sys_account`
    DROP INDEX `uk_account_active_username`,
    DROP COLUMN `active_username`,
    ADD COLUMN `active_username` VARCHAR(50)
        CHARACTER SET utf8mb4 COLLATE utf8mb4_bin
        GENERATED ALWAYS AS (
            CASE WHEN `deleted` = 0 THEN `username` ELSE NULL END
        ) STORED
        COMMENT '未删除账号的大小写敏感用户名',
    ADD UNIQUE KEY `uk_account_active_username` (`active_username`);

-- ==================== 部门编码改为大小写敏感 ====================
ALTER TABLE `sys_department`
    DROP INDEX `uk_department_active_code`,
    DROP COLUMN `active_code`,
    ADD COLUMN `active_code` VARCHAR(50)
        CHARACTER SET utf8mb4 COLLATE utf8mb4_bin
        GENERATED ALWAYS AS (
            CASE WHEN `deleted` = 0 THEN `code` ELSE NULL END
        ) STORED
        COMMENT '未删除部门的大小写敏感编码',
    ADD UNIQUE KEY `uk_department_active_code` (`active_code`);
