package com.sonsure.dumper.test.enums;

import com.sonsure.dumper.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MenuTypeEnum implements BaseEnum {

    ADMIN("1", "admin管理菜单"),

    FRONT("2", "前端菜单"),

    NAV("3", "导航菜单");

    private final String code;
    private final String desc;

}
