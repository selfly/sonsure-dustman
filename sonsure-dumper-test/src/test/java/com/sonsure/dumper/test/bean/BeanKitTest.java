/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.bean;

import com.sonsure.dumper.common.bean.BeanKit;
import com.sonsure.dumper.common.enums.EnumHelper;
import com.sonsure.dumper.common.enums.DynamicEnumItem;
import com.sonsure.dumper.test.enums.ActiveStatusEnum;
import com.sonsure.dumper.test.enums.MenuStatusEnum;
import com.sonsure.dumper.test.enums.MenuTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class BeanKitTest {

    @Test
    public void testCopyProperties() {
        Menu menu = new Menu();
        menu.setMenuId(100L);
        menu.setMenuCode("test_menu");
        menu.setMenuTitle("测试菜单");
        menu.setItsRoute(true);
        menu.setMenuType(MenuTypeEnum.ADMIN.getCode());
        menu.setOrderNo(10);
        menu.setStatus(MenuStatusEnum.NORMAL.getCode());
        menu.setActiveStatus(ActiveStatusEnum.ENABLE.getCode());
        menu.setGmtCreate(LocalDateTime.now());
        menu.setGmtModify(new Date());

        MenuVo menuVo = BeanKit.copyProperties(new MenuVo(), menu);
        Assertions.assertEquals(menu.getMenuId(), menuVo.getMenuId());
        Assertions.assertEquals(menu.getMenuCode(), menuVo.getMenuCode());
        Assertions.assertEquals(menu.getMenuTitle(), menuVo.getMenuTitle());
        Assertions.assertEquals(menu.getItsRoute(), menuVo.getItsRoute());
        Assertions.assertEquals(menu.getMenuType(), menuVo.getMenuType().getCode());
        Assertions.assertEquals(menu.getOrderNo(), menuVo.getOrderNo());
        Assertions.assertEquals(menu.getStatus(), menuVo.getStatus().getCode());
        Assertions.assertEquals(menu.getActiveStatus(), menuVo.getActiveStatus().getCode());
        Assertions.assertEquals(menu.getGmtCreate(), menuVo.getGmtCreate());
        Assertions.assertEquals(menu.getGmtModify(), menuVo.getGmtModify());

        EnumHelper.addDynamicEnumItem(ActiveStatusEnum.class, DynamicEnumItem.of("5", "激活"));

        menu.setActiveStatus("5");
        MenuVo menuVo2 = BeanKit.copyProperties(new MenuVo(), menu);
        Assertions.assertEquals(menuVo2.getActiveStatus().getCode(), "5");
        Assertions.assertEquals(menuVo2.getActiveStatus().getName(), "激活");
    }

}
