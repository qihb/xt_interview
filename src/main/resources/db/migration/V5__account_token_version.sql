-- ==================== 账号 Token 版本 ====================
ALTER TABLE `sys_account`
    ADD COLUMN `token_version` INT NOT NULL DEFAULT 0
        COMMENT 'Token 版本，账号身份变更时递增';
