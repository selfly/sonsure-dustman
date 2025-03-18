package com.sonsure.dumper.common.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author selfly
 */
@Getter
@Setter
@NoArgsConstructor
public class DynamicEnumItem extends DynamicEnum<BaseDynamicEnum> {

    private String code;
    private String name;
    private Object value;
    private String description;
    private String origName;

    private DynamicEnumItem(String code, String name, Object value, String description) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public static DynamicEnumItem of(String code, String name) {
        return of(code, name, null, null);
    }

    public static DynamicEnumItem of(String code, String name, Object value) {
        return of(code, name, value, null);
    }

    public static DynamicEnumItem of(String code, String name, Object value, String description) {
        return new DynamicEnumItem(code, name, value, description);
    }


    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DynamicEnumItem) {
            DynamicEnumItem another = (DynamicEnumItem) obj;
            return this.code.equals(another.getCode());
        }
        return false;
    }

}
