-- ==================== 仅未删除记录保持账号用户名唯一 ====================
ALTER TABLE `sys_account`
    DROP INDEX `uk_username`,
    ADD COLUMN `active_username` VARCHAR(50)
        GENERATED ALWAYS AS (
            CASE WHEN `deleted` = 0 THEN `username` ELSE NULL END
        ) STORED
        COMMENT '未删除账号的唯一用户名',
    ADD UNIQUE KEY `uk_account_active_username` (`active_username`);

-- ==================== 仅未删除记录保持部门编码唯一 ====================
ALTER TABLE `sys_department`
    DROP INDEX `uk_department_code`,
    ADD COLUMN `active_code` VARCHAR(50)
        GENERATED ALWAYS AS (
            CASE WHEN `deleted` = 0 THEN `code` ELSE NULL END
        ) STORED
        COMMENT '未删除部门的唯一编码',
    ADD UNIQUE KEY `uk_department_active_code` (`active_code`);
