package com.sonsure.dumper.test.enums;

import com.sonsure.dumper.common.enums.*;
import com.sonsure.dumper.common.exception.SonsureCommonsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class EnumTest {

    @Test
    public void testGetInitDynamicEnumItems() {
        Collection<MenuStatusEnum> enumItems = EnumHelper.getEnumItems(MenuStatusEnum.class);
        Assertions.assertNotNull(enumItems);
        Collection<MenuTypeEnum> enumItems2 = EnumHelper.getEnumItems(MenuTypeEnum.class);
        Assertions.assertEquals(enumItems2.size(), MenuTypeEnum.values().length);
    }

    @Test
    public void testGetEnumItems() {
        Collection<BaseEnum> enumItems = EnumHelper.getEnumItems(MenuStatusEnum.class);
        Assertions.assertNotNull(enumItems);

        Collection<BaseEnum> enumItems2 = EnumHelper.getEnumItems(MenuTypeEnum.class);
        Assertions.assertEquals(enumItems2.size(), MenuTypeEnum.values().length);
    }

    @Test
    public void testGetEnumItem() {
        BaseEnum enumItem = EnumHelper.getEnumItem(MenuStatusEnum.class, MenuStatusEnum.DISABLE.getCode());
        Assertions.assertEquals(enumItem.getCode(), MenuStatusEnum.DISABLE.getCode());

        BaseEnum enumItem2 = EnumHelper.getEnumItem(MenuTypeEnum.class, MenuTypeEnum.FRONT.getCode());
        Assertions.assertEquals(enumItem2.getCode(), MenuTypeEnum.FRONT.getCode());
    }

    @Test
    public void testGetEnumItemRequired() {
        BaseEnum enumItem = EnumHelper.getEnumItem(MenuStatusEnum.class, "8", false);
        Assertions.assertNull(enumItem);

        SonsureCommonsException sonsureCommonsException = Assertions.assertThrows(SonsureCommonsException.class, () -> EnumHelper.getEnumItem(MenuStatusEnum.class, "8"));
        Assertions.assertEquals("枚举不存在,class:com.sonsure.dumper.test.enums.MenuStatusEnum,code:8", sonsureCommonsException.getMessage());

        BaseEnum enumItem2 = EnumHelper.getEnumItem(MenuTypeEnum.class, "5", false);
        Assertions.assertNull(enumItem2);

        SonsureCommonsException sonsureCommonsException2 = Assertions.assertThrows(SonsureCommonsException.class, () -> EnumHelper.getEnumItem(MenuTypeEnum.class, "8"));
        Assertions.assertEquals("枚举不存在,class:com.sonsure.dumper.test.enums.MenuTypeEnum,code:8", sonsureCommonsException2.getMessage());
    }

    @Test
    public void testGetEnumItemByOrigName() {
        DynamicEnumItem dynamicEnumItemByOrigName = EnumHelper.getDynamicEnumItemByOrigName(MenuStatusEnum.class, MenuStatusEnum.DISABLE.name());
        Assertions.assertEquals(dynamicEnumItemByOrigName.getOrigName(), MenuStatusEnum.DISABLE.name());

    }

    @Test
    public void getFinalEnumItems() {
        Collection<MenuTypeEnum> finalEnumItems = EnumHelper.getFinalEnumItems(MenuTypeEnum.class);
        Assertions.assertEquals(finalEnumItems.size(), MenuTypeEnum.values().length);
    }

    @Test
    public void getFinalEnumItem() {
        MenuTypeEnum finalEnumItem = EnumHelper.getFinalEnumItem(MenuTypeEnum.class, "1");
        Assertions.assertEquals(MenuTypeEnum.ADMIN, finalEnumItem);
    }

    @Test
    public void getDynamicEnumItems() {
        Collection<DynamicEnumItem> dynamicEnumItems = EnumHelper.getDynamicEnumItems(MenuStatusEnum.class);
        Assertions.assertNotNull(dynamicEnumItems);
    }

    @Test
    public void getDynamicEnumItem() {
        DynamicEnumItem dynamicEnumItem = EnumHelper.getDynamicEnumItem(MenuStatusEnum.class, MenuStatusEnum.DISABLE.getCode());
        Assertions.assertEquals(MenuStatusEnum.DISABLE.getCode(), dynamicEnumItem.getCode());

        DynamicEnumItem dynamicEnumItem2 = EnumHelper.getDynamicEnumItem(MenuStatusEnum.class, "8", false);
        Assertions.assertNull(dynamicEnumItem2);
    }


    @Test
    public void getDynamicEnumItemByOrigName() {
        DynamicEnumItem dynamicEnumItemByOrigName = EnumHelper.getDynamicEnumItemByOrigName(MenuStatusEnum.class, MenuStatusEnum.DISABLE.name());
        Assertions.assertEquals(MenuStatusEnum.DISABLE.name(), dynamicEnumItemByOrigName.getOrigName());
    }

    @Test
    public void getGenericEnumItem() {
        DynamicEnum<MenuStatusEnum> genericEnumItem = EnumHelper.getGenericEnumItem(MenuStatusEnum.class, MenuStatusEnum.DISABLE.getCode());
        Assertions.assertEquals(genericEnumItem.getCode(), MenuStatusEnum.DISABLE.getCode());

        DynamicEnum<MenuStatusEnum> genericEnumItem2 = EnumHelper.getGenericEnumItem(MenuStatusEnum.class, "8", false);
        Assertions.assertNull(genericEnumItem2);
    }


    @Test
    public void existsEnumItem() {
        boolean b = EnumHelper.existsEnumItem(MenuStatusEnum.class, "1");
        Assertions.assertTrue(b);

        b = EnumHelper.existsEnumItem(MenuStatusEnum.class, "8");
        Assertions.assertFalse(b);
    }


    @Test
    public void addDynamicEnumItem() {
        EnumHelper.addDynamicEnumItem(MenuStatusEnum.class, DynamicEnumItem.of("5", "测试"));

        DynamicEnumItem dynamicEnumItem = EnumHelper.getDynamicEnumItem(MenuStatusEnum.class, "5");
        Assertions.assertEquals("5", dynamicEnumItem.getCode());
    }

    @Test
    public void removeDynamicEnumItem() {

        SonsureCommonsException sonsureCommonsException = Assertions.assertThrows(SonsureCommonsException.class, () -> {
            EnumHelper.removeDynamicEnumItem(MenuStatusEnum.class, "2");
        });
        Assertions.assertEquals("自带枚举值不允许删除 class com.sonsure.dumper.test.enums.MenuStatusEnum code:2", sonsureCommonsException.getMessage());

        EnumHelper.addDynamicEnumItem(MenuStatusEnum.class, DynamicEnumItem.of("6", "测试66"));
        DynamicEnumItem dynamicEnumItem = EnumHelper.getDynamicEnumItem(MenuStatusEnum.class, "6");
        Assertions.assertEquals("6", dynamicEnumItem.getCode());

        EnumHelper.removeDynamicEnumItem(MenuStatusEnum.class, "6");
        DynamicEnumItem dynamicEnumItem2 = EnumHelper.getDynamicEnumItem(MenuStatusEnum.class, "6", false);
        Assertions.assertNull(dynamicEnumItem2);
    }

    @Test
    public void updateDynamicEnumDesc() {
        DynamicEnumItem dynamicEnumItem = EnumHelper.getDynamicEnumItem(MenuStatusEnum.class, "2");
        Assertions.assertEquals("禁用", dynamicEnumItem.getName());

        EnumHelper.updateDynamicEnumName(MenuStatusEnum.class, "2", "再次禁用");

        DynamicEnumItem dynamicEnumItem2 = EnumHelper.getDynamicEnumItem(MenuStatusEnum.class, "2");
        Assertions.assertEquals("再次禁用", dynamicEnumItem2.getName());
    }

}
