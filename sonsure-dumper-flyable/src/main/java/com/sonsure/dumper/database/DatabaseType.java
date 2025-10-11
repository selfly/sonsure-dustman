package com.sonsure.dumper.database;

import com.sonsure.dumper.common.enums.BaseEnum;
import com.sonsure.dumper.exception.FlyableException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author selfly
 */
@AllArgsConstructor
@Getter
public enum DatabaseType implements BaseEnum {

    /**
     * h2 使用 mysql 模式
     */
    H2("h2", "mysql"),
    MYSQL("mysql", "mysql");

    private final String code;
    private final String name;

    public static DatabaseType resolveDatabase(String databaseProduct) {
        for (DatabaseType databaseType : values()) {
            if (databaseType.getCode().contains(databaseProduct.toLowerCase())) {
                return databaseType;
            }
        }
        throw new FlyableException("当前不支持的数据库");
    }
}
