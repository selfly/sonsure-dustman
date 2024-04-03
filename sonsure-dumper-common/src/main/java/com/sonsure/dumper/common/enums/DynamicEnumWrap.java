package com.sonsure.dumper.common.enums;

/**
 * @author selfly
 */
public class DynamicEnumWrap implements BaseDynamicEnum {

    private final Class<? extends BaseDynamicEnum> cls;

    public DynamicEnumWrap(Class<? extends BaseDynamicEnum> cls) {
        this.cls = cls;
    }

    @Override
    public Class<? extends BaseDynamicEnum> getEnumClass() {
        return this.cls;
    }

    public static DynamicEnumWrap of(Class<? extends BaseDynamicEnum> cls) {
        return new DynamicEnumWrap(cls);
    }
}
