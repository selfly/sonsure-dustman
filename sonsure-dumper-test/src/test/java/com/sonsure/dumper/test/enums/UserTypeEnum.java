package com.sonsure.dumper.test.enums;


import com.sonsure.dumper.common.enums.BaseDynamicEnum;

public enum UserTypeEnum implements BaseDynamicEnum {

    ADMIN("1", "管理员"),

    USER("2", "用户"),

    VIP("3", "会员");

    UserTypeEnum(String code, String desc) {
        putEnum(code, desc);
    }


}
