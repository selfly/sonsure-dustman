package com.sonsure.dumper.common.enums;

/**
 * 动态枚举
 * 偷天换日方式实现，不支持在osgi环境下使用
 *
 * @author selfly
 */
public interface BaseDynamicEnum extends BaseEnum {

    /**
     * Gets enum class.
     *
     * @return the enum class
     */
    default Class<? extends BaseDynamicEnum> getEnumClass() {
        return this.getClass();
    }

    /**
     * Put enum.
     *
     * @param code the code
     * @param desc the desc
     */
    default void putEnum(String code, String desc) {
        putEnum(new DynamicEnumItem(code, desc));
    }

    /**
     * Put enum.
     *
     * @param enumItem the enum item
     */
    default void putEnum(DynamicEnumItem enumItem) {
        DynamicEnumHelper.addEnumItem(this, enumItem);
    }

    /**
     * Gets enum item.
     *
     * @return the enum item
     */
    default DynamicEnumItem getEnumItem() {
        return DynamicEnumHelper.getEnumItem(this);
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    default String getCode() {
        return getEnumItem().getCode();
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    default String getDesc() {
        return getEnumItem().getDesc();
    }

}
