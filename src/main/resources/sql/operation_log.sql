CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(64) NOT NULL COMMENT '操作人用户名',
    role VARCHAR(32) DEFAULT NULL COMMENT '操作人角色',
    operation VARCHAR(128) NOT NULL COMMENT '操作名称',
    request_path VARCHAR(255) NOT NULL COMMENT '请求路径',
    success TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功: 1成功 0失败',
    cost_ms BIGINT NOT NULL DEFAULT 0 COMMENT '耗时毫秒',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_create_time (create_time),
    INDEX idx_username (username),
    INDEX idx_success (success)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';
