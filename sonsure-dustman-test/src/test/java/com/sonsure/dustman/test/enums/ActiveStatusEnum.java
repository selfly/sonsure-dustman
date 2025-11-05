package com.sonsure.dustman.test.enums;


import com.sonsure.dustman.common.enums.BaseDynamicEnum;

public enum ActiveStatusEnum implements BaseDynamicEnum {

    ENABLE("1", "启用"),
    DISABLE("2", "禁用");

    ActiveStatusEnum(String code, String desc) {
        putEnum(code, desc);
    }
}
