package com.sonsure.dustman.common.enums;

import com.sonsure.dustman.common.exception.SonsureCommonsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author selfly
 */
public class EnumHelper {

    private static final Map<Class<? extends BaseDynamicEnum>, Map<BaseDynamicEnum, DynamicEnumItem>> ENUM_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends BaseEnum> Collection<T> getEnumItems(Class<? extends BaseEnum> enumCls) {
        if (BaseDynamicEnum.class.isAssignableFrom(enumCls)) {
            return (Collection<T>) computeIfAbsentWithClass((Class<? extends BaseDynamicEnum>) enumCls).values();
        } else {
            return (Collection<T>) getFinalEnumItems(enumCls);
        }
    }

    public static <T extends BaseEnum> T getEnumItem(Class<T> enumCls, String code) {
        return getEnumItem(enumCls, code, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEnum> T getEnumItem(Class<T> enumCls, String code, boolean required) {
        T enumItem;
        if (BaseDynamicEnum.class.isAssignableFrom(enumCls)) {
            enumItem = (T) getDynamicEnumItem((Class<? extends BaseDynamicEnum>) enumCls, code, required);
        } else {
            enumItem = getFinalEnumItem(enumCls, code, required);
        }
        return enumItem;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEnum> Collection<T> getFinalEnumItems(Class<T> enumCls) {
        BaseEnum[] enumConstants = enumCls.getEnumConstants();
        return (Collection<T>) Arrays.asList(enumConstants);
    }

    public static <T extends BaseEnum> T getFinalEnumItem(Class<T> enumCls, String code) {
        return getFinalEnumItem(enumCls, code, true);
    }

    public static <T extends BaseEnum> T getFinalEnumItem(Class<T> enumCls, String code, boolean required) {
        T val = getFinalEnumItems(enumCls).stream()
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
        if (required && val == null) {
            throw new SonsureCommonsException("枚举不存在,class:" + enumCls.getName() + ",code:" + code);
        }
        return val;
    }

    public static Collection<DynamicEnumItem> getDynamicEnumItems(Class<? extends BaseDynamicEnum> enumCls) {
        return computeIfAbsentWithClass(enumCls).values();
    }

    /**
     * Resolve by code enum item.
     *
     * @param enumCls the enum cls
     * @param code    the code
     * @return the enum item
     */
    public static DynamicEnumItem getDynamicEnumItem(Class<? extends BaseDynamicEnum> enumCls, String code) {
        return getDynamicEnumItem(enumCls, code, true);
    }

    /**
     * Gets enum item.
     *
     * @param enumCls  the enum cls
     * @param code     the code
     * @param required the required
     * @return the enum item
     */
    public static DynamicEnumItem getDynamicEnumItem(Class<? extends BaseDynamicEnum> enumCls, String code, boolean required) {
        Collection<DynamicEnumItem> enumItems = getDynamicEnumItems(enumCls);
        for (DynamicEnumItem enumItem : enumItems) {
            if (enumItem.getCode().equalsIgnoreCase(code)) {
                return enumItem;
            }
        }
        if (required) {
            throw new SonsureCommonsException("枚举不存在,class:" + enumCls.getName() + ",code:" + code);
        }
        return null;
    }

    public static DynamicEnumItem getDynamicEnumItemByOrigName(Class<? extends BaseDynamicEnum> enumCls, String origName) {
        Collection<DynamicEnumItem> enumItems = getDynamicEnumItems(enumCls);
        for (DynamicEnumItem enumItem : enumItems) {
            if (enumItem.getOrigName().equalsIgnoreCase(origName)) {
                return enumItem;
            }
        }
        throw new SonsureCommonsException("枚举不存在,class:" + enumCls.getName() + ",origName:" + origName);
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
        return getGenericEnumItem(enumCls, code, true);
    }

    /**
     * Gets generic enum item.
     *
     * @param <T>      the type parameter
     * @param enumCls  the enum cls
     * @param code     the code
     * @param required the required
     * @return the generic enum item
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseDynamicEnum> DynamicEnum<T> getGenericEnumItem(Class<T> enumCls, String code, boolean required) {
        return (DynamicEnum<T>) getDynamicEnumItem(enumCls, code, required);
    }

    /**
     * Exists enum item boolean.
     *
     * @param enumCls the enum cls
     * @param code    the code
     * @return the boolean
     */
    public static boolean existsEnumItem(Class<? extends BaseDynamicEnum> enumCls, String code) {
        return getDynamicEnumItem(enumCls, code, false) != null;
    }

    /**
     * Put enum.
     *
     * @param enumType the enum type
     * @param enumItem the enum item
     */
    static void addDynamicEnumItem(BaseDynamicEnum enumType, DynamicEnumItem enumItem) {
        Map<BaseDynamicEnum, DynamicEnumItem> map = computeIfAbsentWithClass(enumType.getEnumClass());
        for (BaseDynamicEnum type : map.keySet()) {
            if (type.getCode().equalsIgnoreCase(enumItem.getCode())) {
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
    public static void addDynamicEnumItem(Class<? extends BaseDynamicEnum> enumTypeCls, DynamicEnumItem enumItem) {
        addDynamicEnumItem(DynamicEnumWrap.of(enumTypeCls), enumItem);
    }

    /**
     * Remove enum item.
     *
     * @param enumCls the enum cls
     * @param code    the code
     */
    public static void removeDynamicEnumItem(Class<? extends BaseDynamicEnum> enumCls, String code) {
        BaseDynamicEnum[] enumConstants = enumCls.getEnumConstants();
        for (BaseDynamicEnum enumConstant : enumConstants) {
            if (enumConstant.getCode().equalsIgnoreCase(code)) {
                throw new SonsureCommonsException("自带枚举值不允许删除 " + enumCls + " code:" + code);
            }
        }
        Map<BaseDynamicEnum, DynamicEnumItem> map = computeIfAbsentWithClass(enumCls);
        map.entrySet().removeIf(next -> next.getKey().getCode().equalsIgnoreCase(code));
    }

    /**
     * Update enum desc.
     *
     * @param enumCls the enum cls
     * @param code    the code
     * @param name    the name
     */
    public static void updateDynamicEnumName(Class<? extends BaseDynamicEnum> enumCls, String code, String name) {
        Collection<? extends BaseEnum> enumItems = getDynamicEnumItems(enumCls);
        boolean notExists = true;
        for (BaseEnum enumItem : enumItems) {
            DynamicEnumItem en = (DynamicEnumItem) enumItem;
            if (en.getCode().equalsIgnoreCase(code)) {
                en.setName(name);
                notExists = false;
            }
        }
        if (notExists) {
            throw new SonsureCommonsException("指定的枚举值不存在 " + enumCls + " code:" + code);
        }
    }

    /**
     * Gets enum.
     *
     * @param enumType the enum type
     * @return the enum
     */
    static DynamicEnumItem getDynamicEnumItem(BaseDynamicEnum enumType) {
        return Optional.ofNullable(computeIfAbsentWithClass(enumType.getEnumClass()))
                .map(v -> v.get(enumType))
                .orElseThrow(() -> new SonsureCommonsException("枚举不存在,class:" + enumType.getClass() + ",code:" + enumType.getCode()));
    }

    private static Map<BaseDynamicEnum, DynamicEnumItem> computeIfAbsentWithClass(Class<? extends BaseDynamicEnum> enumCls) {
        //init enum class
        enumCls.getEnumConstants();
        return ENUM_CACHE.computeIfAbsent(enumCls, key -> new LinkedHashMap<>(16));
    }

}
