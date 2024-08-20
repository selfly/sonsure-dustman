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

    @Setter
    private String desc;

    private DynamicEnumItem(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DynamicEnumItem of(String code, String desc) {
        return new DynamicEnumItem(code, desc);
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
