package com.sonsure.dumper.test.bean;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class Menu {

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
     * 类型;1:管理菜单,2:导航菜单
     */
    private String menuType;

    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 状态;1:正常,2:隐藏,3:禁用
     */
    private String status;

    private String activeStatus;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;
}
