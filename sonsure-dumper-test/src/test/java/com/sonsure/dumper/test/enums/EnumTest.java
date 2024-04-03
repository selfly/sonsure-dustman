package com.sonsure.dumper.test.enums;

import com.sonsure.dumper.common.enums.DynamicEnumHelper;
import com.sonsure.dumper.common.enums.DynamicEnumItem;
import com.sonsure.dumper.common.exception.SonsureCommonsException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EnumTest {

    @Test
    public void testGetInitDynamicEnumItems() {
        List<DynamicEnumItem> enumItems = DynamicEnumHelper.getEnumItems(MenuStatusEnum.class);
        Assert.assertEquals(enumItems.size(), MenuTypeEnum.values().length);
    }

    /**
     * 全局缓存避免相互影响，变更操作同一方法测试
     */
    @Test
    public void testDynamicEnumItems() {
        UserTypeEnum[] values = UserTypeEnum.values();
        Assert.assertEquals(3, values.length);
        Assert.assertEquals("1", values[0].getCode());
        Assert.assertEquals("管理员", values[0].getDesc());
        Assert.assertEquals("2", values[1].getCode());
        Assert.assertEquals("用户", values[1].getDesc());
        Assert.assertEquals("3", values[2].getCode());
        Assert.assertEquals("会员", values[2].getDesc());

        List<DynamicEnumItem> enumValues = DynamicEnumHelper.getEnumItems(UserTypeEnum.class);
        Assert.assertEquals(values.length, enumValues.size());
        Assert.assertEquals(enumValues.size(), values.length);
        for (int i = 0; i < enumValues.size(); i++) {
            Assert.assertEquals(values[i].getCode(), enumValues.get(i).getCode());
            Assert.assertEquals(values[i].getDesc(), enumValues.get(i).getDesc());
        }

        DynamicEnumItem enumItem = DynamicEnumHelper.getEnumItemByCode(UserTypeEnum.class, "2");
        Assert.assertEquals("2", enumItem.getCode());
        Assert.assertEquals("用户", enumItem.getDesc());

        DynamicEnumHelper.addEnumItem(UserTypeEnum.class, DynamicEnumItem.of("4", "商家"));
        DynamicEnumHelper.addEnumItem(UserTypeEnum.class, DynamicEnumItem.of("5", "店员"));

        List<DynamicEnumItem> newEnumItems = DynamicEnumHelper.getEnumItems(UserTypeEnum.class);
        Assert.assertEquals(newEnumItems.size(), 5);

        Assert.assertEquals(newEnumItems.get(0).getCode(), "1");
        Assert.assertEquals(newEnumItems.get(0).getDesc(), "管理员");
        Assert.assertEquals(newEnumItems.get(1).getCode(), "2");
        Assert.assertEquals(newEnumItems.get(1).getDesc(), "用户");
        Assert.assertEquals(newEnumItems.get(2).getCode(), "3");
        Assert.assertEquals(newEnumItems.get(2).getDesc(), "会员");
        Assert.assertEquals(newEnumItems.get(3).getCode(), "4");
        Assert.assertEquals(newEnumItems.get(3).getDesc(), "商家");
        Assert.assertEquals(newEnumItems.get(4).getCode(), "5");
        Assert.assertEquals(newEnumItems.get(4).getDesc(), "店员");

        DynamicEnumHelper.removeEnumItem(UserTypeEnum.class, "4");

        List<DynamicEnumItem> removedEnumItems = DynamicEnumHelper.getEnumItems(UserTypeEnum.class);
        Assert.assertEquals(removedEnumItems.size(), 4);

        DynamicEnumHelper.updateEnumDesc(UserTypeEnum.class, "3", "特殊用户");

        Assert.assertEquals(removedEnumItems.get(0).getCode(), "1");
        Assert.assertEquals(removedEnumItems.get(0).getDesc(), "管理员");
        Assert.assertEquals(removedEnumItems.get(1).getCode(), "2");
        Assert.assertEquals(removedEnumItems.get(1).getDesc(), "用户");
        Assert.assertEquals(removedEnumItems.get(2).getCode(), "3");
        Assert.assertEquals(removedEnumItems.get(2).getDesc(), "特殊用户");
        Assert.assertEquals(removedEnumItems.get(3).getCode(), "5");
        Assert.assertEquals(removedEnumItems.get(3).getDesc(), "店员");
    }

    @Test(expected = SonsureCommonsException.class)
    public void testCodeExist() {
        UserTypeEnum user = UserTypeEnum.USER;
        Assert.assertEquals(user.getCode(), "2");
        DynamicEnumHelper.addEnumItem(UserTypeEnum.class, DynamicEnumItem.of("2", "code已存在测试"));
    }

    @Test(expected = SonsureCommonsException.class)
    public void testRemoveDefineItem() {
        UserTypeEnum user = UserTypeEnum.USER;
        Assert.assertEquals(user.getCode(), "2");
        DynamicEnumHelper.removeEnumItem(UserTypeEnum.class, "2");
    }

    @Test(expected = SonsureCommonsException.class)
    public void testCodeNotExist() {
        DynamicEnumHelper.updateEnumDesc(UserTypeEnum.class, "9", "code不存在测试");
    }

}
