-- ==================== 账号部门关联 ====================
ALTER TABLE `sys_account`
    ADD COLUMN `department_id` BIGINT NULL COMMENT '所属部门ID' AFTER `is_super`,
    ADD INDEX `idx_account_department` (`department_id`),
    ADD CONSTRAINT `fk_account_department`
        FOREIGN KEY (`department_id`) REFERENCES `sys_department` (`id`)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT;
