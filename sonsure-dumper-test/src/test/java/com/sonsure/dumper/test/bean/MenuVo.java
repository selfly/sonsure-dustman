package com.sonsure.dumper.test.bean;

import com.sonsure.dumper.common.enums.DynamicEnum;
import com.sonsure.dumper.test.enums.ActiveStatusEnum;
import com.sonsure.dumper.test.enums.MenuStatusEnum;
import com.sonsure.dumper.test.enums.MenuTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class MenuVo {


    /**
     * 主键id
     */
    private Long menuId;

    /**
     * 编码
     */
    private String menuCode;

    /**
     * title
     */
    private String menuTitle;

    /**
     * 是否路由
     */
    private Boolean itsRoute;

    /**
     * 类型 固定枚举
     */
    private MenuTypeEnum menuType;

    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 状态; 动态枚举常规使用
     */
    private MenuStatusEnum status;

    /**
     * 状态; 动态枚举动态使用
     */
    private DynamicEnum<ActiveStatusEnum> activeStatus;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;
}
