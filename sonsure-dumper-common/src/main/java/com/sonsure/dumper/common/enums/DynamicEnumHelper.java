package com.sonsure.dumper.common.enums;

import com.sonsure.dumper.common.exception.SonsureCommonsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author selfly
 */
public class DynamicEnumHelper {

    private static final Map<Class<? extends BaseDynamicEnum>, Map<BaseDynamicEnum, DynamicEnumItem>> ENUM_CACHE = new ConcurrentHashMap<>();

    public static List<DynamicEnumItem> getEnumItems(Class<? extends BaseDynamicEnum> enumCls) {
        return new ArrayList<>(computeIfAbsentWithClass(enumCls).values());
    }

    /**
     * Gets enum.
     *
     * @param enumType the enum type
     * @return the enum
     */
    static DynamicEnumItem getEnumItem(BaseDynamicEnum enumType) {
        return Optional.ofNullable(computeIfAbsentWithClass(enumType.getEnumClass()))
                .map(v -> v.get(enumType))
                .orElseThrow(() -> new SonsureCommonsException("枚举不存在:" + enumType.getCode()));
    }

    /**
     * Resolve by code enum item.
     *
     * @param enumCls the enum cls
     * @param code    the code
     * @return the enum item
     */
    public static DynamicEnumItem getEnumItem(Class<? extends BaseDynamicEnum> enumCls, String code) {
        for (Map.Entry<BaseDynamicEnum, DynamicEnumItem> entry : computeIfAbsentWithClass(enumCls).entrySet()) {
            if (entry.getKey().getCode().equals(code)) {
                return entry.getValue();
            }
        }
        throw new SonsureCommonsException("枚举不存在:" + code);
    }

    /**
     * Gets dynamic enum item by code.
     *
     * @param <T>     the type parameter
     * @param enumCls the enum cls
     * @param code    the code
     * @return the dynamic enum item by code
     */
    public static <T extends BaseDynamicEnum> DynamicEnum<T> getGenericEnumItem(Class<T> enumCls, String code) {
        //noinspection unchecked
        return (DynamicEnum<T>) getEnumItem(enumCls, code);
    }

    /**
     * Exists enum item boolean.
     *
     * @param enumCls the enum cls
     * @param code    the code
     * @return the boolean
     */
    public static boolean existsEnumItem(Class<? extends BaseDynamicEnum> enumCls, String code) {
        for (Map.Entry<BaseDynamicEnum, DynamicEnumItem> entry : computeIfAbsentWithClass(enumCls).entrySet()) {
            if (entry.getKey().getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Put enum.
     *
     * @param enumType the enum type
     * @param enumItem the enum item
     */
    static void addEnumItem(BaseDynamicEnum enumType, DynamicEnumItem enumItem) {
        Map<BaseDynamicEnum, DynamicEnumItem> map = computeIfAbsentWithClass(enumType.getEnumClass());
        for (BaseDynamicEnum type : map.keySet()) {
            if (type.getCode().equals(enumItem.getCode())) {
                throw new SonsureCommonsException(enumType.getEnumClass() + " code已经存在:" + enumItem.getCode());
            }
        }
        map.put(enumType, enumItem);
    }


    /**
     * Add enum item.
     *
     * @param enumTypeCls the enum type cls
     * @param enumItem    the enum item
     */
    public static void addEnumItem(Class<? extends BaseDynamicEnum> enumTypeCls, DynamicEnumItem enumItem) {
        addEnumItem(DynamicEnumWrap.of(enumTypeCls), enumItem);
    }

    /**
     * Remove enum item.
     *
     * @param enumCls the enum cls
     * @param code    the code
     */
    public static void removeEnumItem(Class<? extends BaseDynamicEnum> enumCls, String code) {
        BaseDynamicEnum[] enumConstants = enumCls.getEnumConstants();
        for (BaseDynamicEnum enumConstant : enumConstants) {
            if (enumConstant.getCode().equals(code)) {
                throw new SonsureCommonsException("自带枚举值不允许删除 " + enumCls + " code:" + code);
            }
        }
        Map<BaseDynamicEnum, DynamicEnumItem> map = computeIfAbsentWithClass(enumCls);
        map.entrySet().removeIf(next -> next.getKey().getCode().equals(code));
    }

    /**
     * Update enum desc.
     *
     * @param enumCls the enum cls
     * @param code    the code
     * @param name    the name
     */
    public static void updateEnumDesc(Class<? extends BaseDynamicEnum> enumCls, String code, String name) {
        List<DynamicEnumItem> enumItems = getEnumItems(enumCls);
        boolean notExists = true;
        for (DynamicEnumItem enumItem : enumItems) {
            if (enumItem.getCode().equals(code)) {
                enumItem.setName(name);
                notExists = false;
            }
        }
        if (notExists) {
            throw new SonsureCommonsException("指定的枚举值不存在 " + enumCls + " code:" + code);
        }
    }

    private static Map<BaseDynamicEnum, DynamicEnumItem> computeIfAbsentWithClass(Class<? extends BaseDynamicEnum> enumCls) {
        //init enum class
        enumCls.getEnumConstants();
        return ENUM_CACHE.computeIfAbsent(enumCls, key -> new LinkedHashMap<>(16));
    }

}
