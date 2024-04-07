package com.sonsure.dumper.test.enums;

import com.sonsure.dumper.common.enums.DynamicEnumHelper;
import com.sonsure.dumper.common.enums.DynamicEnumItem;
import com.sonsure.dumper.common.exception.SonsureCommonsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class EnumTest {

    @Test
    public void testGetInitDynamicEnumItems() {
        List<DynamicEnumItem> enumItems = DynamicEnumHelper.getEnumItems(MenuStatusEnum.class);
        Assertions.assertEquals(enumItems.size(), MenuTypeEnum.values().length);
    }

    /**
     * 全局缓存避免相互影响，变更操作同一方法测试
     */
    @Test
    public void testDynamicEnumItems() {
        UserTypeEnum[] values = UserTypeEnum.values();
        Assertions.assertEquals(3, values.length);
        Assertions.assertEquals("1", values[0].getCode());
        Assertions.assertEquals("管理员", values[0].getDesc());
        Assertions.assertEquals("2", values[1].getCode());
        Assertions.assertEquals("用户", values[1].getDesc());
        Assertions.assertEquals("3", values[2].getCode());
        Assertions.assertEquals("会员", values[2].getDesc());

        List<DynamicEnumItem> enumValues = DynamicEnumHelper.getEnumItems(UserTypeEnum.class);
        Assertions.assertEquals(values.length, enumValues.size());
        Assertions.assertEquals(enumValues.size(), values.length);
        for (int i = 0; i < enumValues.size(); i++) {
            Assertions.assertEquals(values[i].getCode(), enumValues.get(i).getCode());
            Assertions.assertEquals(values[i].getDesc(), enumValues.get(i).getDesc());
        }

        DynamicEnumItem enumItem = DynamicEnumHelper.getEnumItemByCode(UserTypeEnum.class, "2");
        Assertions.assertEquals("2", enumItem.getCode());
        Assertions.assertEquals("用户", enumItem.getDesc());

        DynamicEnumHelper.addEnumItem(UserTypeEnum.class, DynamicEnumItem.of("4", "商家"));
        DynamicEnumHelper.addEnumItem(UserTypeEnum.class, DynamicEnumItem.of("5", "店员"));

        List<DynamicEnumItem> newEnumItems = DynamicEnumHelper.getEnumItems(UserTypeEnum.class);
        Assertions.assertEquals(newEnumItems.size(), 5);
        Assertions.assertEquals(newEnumItems.get(0).getCode(), "1");
        Assertions.assertEquals(newEnumItems.get(0).getDesc(), "管理员");
        Assertions.assertEquals(newEnumItems.get(1).getCode(), "2");
        Assertions.assertEquals(newEnumItems.get(1).getDesc(), "用户");
        Assertions.assertEquals(newEnumItems.get(2).getCode(), "3");
        Assertions.assertEquals(newEnumItems.get(2).getDesc(), "会员");
        Assertions.assertEquals(newEnumItems.get(3).getCode(), "4");
        Assertions.assertEquals(newEnumItems.get(3).getDesc(), "商家");
        Assertions.assertEquals(newEnumItems.get(4).getCode(), "5");
        Assertions.assertEquals(newEnumItems.get(4).getDesc(), "店员");

        DynamicEnumHelper.removeEnumItem(UserTypeEnum.class, "4");

        List<DynamicEnumItem> removedEnumItems = DynamicEnumHelper.getEnumItems(UserTypeEnum.class);
        Assertions.assertEquals(removedEnumItems.size(), 4);

        DynamicEnumHelper.updateEnumDesc(UserTypeEnum.class, "3", "特殊用户");

        Assertions.assertEquals(removedEnumItems.get(0).getCode(), "1");
        Assertions.assertEquals(removedEnumItems.get(0).getDesc(), "管理员");
        Assertions.assertEquals(removedEnumItems.get(1).getCode(), "2");
        Assertions.assertEquals(removedEnumItems.get(1).getDesc(), "用户");
        Assertions.assertEquals(removedEnumItems.get(2).getCode(), "3");
        Assertions.assertEquals(removedEnumItems.get(2).getDesc(), "特殊用户");
        Assertions.assertEquals(removedEnumItems.get(3).getCode(), "5");
        Assertions.assertEquals(removedEnumItems.get(3).getDesc(), "店员");
    }

    @Test
    public void testCodeExist() {
        UserTypeEnum user = UserTypeEnum.USER;
        Assertions.assertEquals(user.getCode(), "2");
        Assertions.assertThrows(SonsureCommonsException.class, () -> {
            DynamicEnumHelper.addEnumItem(UserTypeEnum.class, DynamicEnumItem.of("2", "code已存在测试"));
        });
    }

//    @Test(expected = SonsureCommonsException.class)
//    public void testRemoveDefineItem() {
//        UserTypeEnum user = UserTypeEnum.USER;
//        Assertions.assertEquals(user.getCode(), "2");
//        DynamicEnumHelper.removeEnumItem(UserTypeEnum.class, "2");
//    }
//
//    @Test(Assertions = SonsureCommonsException.class)
//    public void testCodeNotExist() {
//        DynamicEnumHelper.updateEnumDesc(UserTypeEnum.class, "9", "code不存在测试");
//    }

}
