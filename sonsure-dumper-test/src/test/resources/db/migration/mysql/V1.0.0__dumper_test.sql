CREATE TABLE `sd_user_info` (
  `user_info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `login_name` varchar(64) NULL comment 'login_name',
  `password` varchar(64) NULL COMMENT 'password',
  `user_age` int NULL COMMENT 'userAge',
  `gmt_create` datetime NULL COMMENT 'gmtCreate',
  `gmt_modify` datetime NULL COMMENT 'gmtModify',
  PRIMARY KEY (`user_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='用户';

CREATE TABLE `sd_account` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `login_name` varchar(64) NULL comment 'login_name',
  `account_name` varchar(64) NULL comment 'account_name',
  `password` varchar(64) NULL COMMENT 'password',
  `user_age` int NULL COMMENT 'userAge',
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='注解测试';

CREATE TABLE `sm_user_info` (
  `user_info_id_` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `login_name_` varchar(64) NULL comment 'login_name',
  `password_` varchar(64) NULL COMMENT 'password',
  `user_age_` int NULL COMMENT 'userAge',
  `gmt_create_` datetime NULL COMMENT 'gmtCreate',
  `gmt_modify_` datetime NULL COMMENT 'gmtModify',
  PRIMARY KEY (`user_info_id_`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='注解测试';


