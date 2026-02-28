CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '采购单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0待审核 1已入库 2已作废',
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '总金额',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    creator VARCHAR(64) NOT NULL COMMENT '创建人',
    audit_by VARCHAR(64) DEFAULT NULL COMMENT '审核人',
    audit_time DATETIME DEFAULT NULL COMMENT '审核时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单主表';

CREATE TABLE IF NOT EXISTS purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '采购单ID',
    goods_id BIGINT NOT NULL COMMENT '商品ID',
    qty BIGINT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '采购价',
    amount DECIMAL(12,2) NOT NULL COMMENT '金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_goods_id (goods_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细表';
