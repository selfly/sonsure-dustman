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
     * @param name the name
     */
    default void putEnum(String code, String name) {
        putEnum(DynamicEnumItem.of(code, name));
    }

    /**
     * Put enum.
     *
     * @param enumItem the enum item
     */
    default void putEnum(DynamicEnumItem enumItem) {
        enumItem.setOrigName(((Enum<?>) this).name());
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
    @Override
    default String getCode() {
        return getEnumItem().getCode();
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    @Override
    default String getName() {
        return getEnumItem().getName();
    }

    default String getOrigName() {
        return getEnumItem().getOrigName();
    }

}
