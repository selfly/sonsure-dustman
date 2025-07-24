package com.sonsure.dumper.common.enums;

/**
 * @author selfly
 */
public abstract class DynamicEnum<T extends BaseDynamicEnum> implements BaseEnum, ValueEnum, DescriptionEnum {

    @SuppressWarnings("unchecked")
    public static <T extends DynamicEnum<?>> T of(BaseEnum baseEnum) {
        if (baseEnum instanceof DynamicEnumItem) {
            //noinspection unchecked
            return (T) baseEnum;
        }
        return of(baseEnum.getCode(), baseEnum.getName());
    }

    public static <T extends DynamicEnum<?>> T of(String code) {
        return of(DynamicEnumItem.of(code, ""));
    }

    @SuppressWarnings("unchecked")
    public static <T extends DynamicEnum<?>> T of(String code, String name) {
        //noinspection unchecked
        return (T) DynamicEnumItem.of(code, name);
    }

}
