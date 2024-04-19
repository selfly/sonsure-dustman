package com.sonsure.dumper.common.enums;

public abstract class DynamicEnum<T extends BaseDynamicEnum> implements BaseEnum {

    public static <T extends DynamicEnum<?>> T of(BaseDynamicEnum baseDynamicEnum) {
        //noinspection unchecked
        return (T) DynamicEnumItem.of(baseDynamicEnum.getCode(), baseDynamicEnum.getDesc());
    }

    public static <T extends DynamicEnum<?>> T of(DynamicEnumItem dynamicEnumItem) {
        //noinspection unchecked
        return (T) dynamicEnumItem;
    }

}
