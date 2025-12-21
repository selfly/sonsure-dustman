CREATE TABLE `${flyableTableName}` (
    `flyable_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `migration_group` varchar(64) NOT NULL COMMENT '分组',
    `version` varchar(64) NOT NULL COMMENT '版本',
    `description` varchar(256) NOT NULL COMMENT '说明',
    `script` varchar(256) NOT NULL COMMENT '脚本',
    `checksum` varchar(32) NOT NULL COMMENT 'checksum',
    `execution_time` bigint DEFAULT NULL COMMENT '执行耗时',
    `gmt_installed` datetime NOT NULL COMMENT '安装时间',
    `success` tinyint NOT NULL COMMENT '执行结果',
    PRIMARY KEY (`flyable_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 100000 DEFAULT CHARSET = utf8mb4 COMMENT = 'flyable';


