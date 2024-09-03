package com.sonsure.dumper.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 */
public class EnumHelper {

    /**
     * 转换
     * osgi
     *
     * @param <T>      the type parameter
     * @param enumType the enum type
     * @param code     the code
     * @return i enum
     */
    public static <T extends BaseEnum> T getEnum(Class<T> enumType, String code) {
        final T[] enumConstants = enumType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            if (StringUtils.equals(enumConstant.getCode(), code)) {
                return enumConstant;
            }
        }
        return null;
    }
}
