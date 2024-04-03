package com.sonsure.dumper.common.enums;

public abstract class DynamicEnum<T extends BaseDynamicEnum> implements BaseEnum {

    @SuppressWarnings("unchecked")
    public static <T extends DynamicEnum<?>> T of(BaseDynamicEnum baseDynamicEnum) {
        return (T) DynamicEnumItem.of(baseDynamicEnum.getCode(), baseDynamicEnum.getDesc());
    }

}
