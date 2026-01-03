package com.sonsure.dustman.common.enums;

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
        EnumHelper.addDynamicEnumItem(this, enumItem);
    }

    /**
     * Gets enum item.
     *
     * @return the enum item
     */
    default DynamicEnumItem getEnumItem() {
        return EnumHelper.getDynamicEnumItem(this);
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

    /**
     * Gets orig name.
     *
     * @return the orig name
     */
    default String getOrigName() {
        return getEnumItem().getOrigName();
    }

    /**
     * To dynamic enum t.
     *
     * @param <T> the type parameter
     * @return the t
     */
    default <T extends DynamicEnum<?>> T toDynamicEnum() {
        return DynamicEnumItem.of(this);
    }


}
