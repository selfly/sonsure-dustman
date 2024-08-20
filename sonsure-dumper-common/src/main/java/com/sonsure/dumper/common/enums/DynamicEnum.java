package com.sonsure.dumper.common.enums;

public abstract class DynamicEnum<T extends BaseDynamicEnum> implements BaseEnum {

    public static <T extends DynamicEnum<?>> T of(BaseEnum baseEnum) {
        if (baseEnum instanceof DynamicEnumItem) {
            //noinspection unchecked
            return (T) baseEnum;
        }
        return of(baseEnum.getCode(), baseEnum.getDesc());
    }

    public static <T extends DynamicEnum<?>> T of(String code) {
        return of(DynamicEnumItem.of(code, ""));
    }

    public static <T extends DynamicEnum<?>> T of(String code, String desc) {
        //noinspection unchecked
        return (T) DynamicEnumItem.of(code, desc);
    }

}
