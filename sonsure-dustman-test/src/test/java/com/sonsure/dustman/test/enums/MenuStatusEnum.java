package com.sonsure.dustman.test.enums;

import com.sonsure.dustman.common.enums.BaseDynamicEnum;

public enum MenuStatusEnum implements BaseDynamicEnum {

    NORMAL("1", "正常"),

    DISABLE("2", "禁用"),

    HIDE("3", "隐藏");

    MenuStatusEnum(String code, String desc) {
        putEnum(code, desc);
    }
}
