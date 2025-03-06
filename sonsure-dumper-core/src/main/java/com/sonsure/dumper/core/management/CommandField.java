///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   http://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.core.management;
//
//import com.sonsure.dumper.core.exception.SonsureJdbcException;
//import org.apache.commons.lang3.StringUtils;
//
///**
// * @author liyd
// * @date 17/4/11
// */
//public class CommandField {
//
//    /**
//     * value需要native内容前后包围符号
//     */
//    public static final String NATIVE_FIELD_OPEN_TOKEN = "{{";
//    public static final String NATIVE_FIELD_CLOSE_TOKEN = "}}";
//
//    /**
//     * 名称
//     */
//    private String fieldName;
//
//    /**
//     * 表别名
//     */
//    private String tableAlias;
//
//    /**
//     * 逻辑操作符 and or
//     */
//    private String logicalOperator;
//
//    /**
//     * 属性操作符 大于、小于、in等
//     */
//    private String fieldOperator;
//
//    /**
//     * 值
//     */
//    private Object value;
//
//    /**
//     * field所属class，非entity操作可能为null
//     */
//    private Class<?> cls;
//
//    /**
//     * The Is native.
//     */
//    private boolean isNative = false;
//
//    /**
//     * 类型
//     */
//    private Type type;
//
//    public CommandField(String fieldName, boolean analyseTableAlias, Type type) {
//        this(fieldName, analyseTableAlias, type, null);
//    }
//
//    public CommandField(String fieldName, boolean analyseTableAlias, Type type, Class<?> cls) {
//        this.fieldName = fieldName;
//        this.type = type;
//        if (type == Type.ENTITY_FIELD && cls == null) {
//            throw new SonsureJdbcException("ENTITY_FIELD Field必须指定class");
//        }
//        if (StringUtils.startsWith(fieldName, CommandField.NATIVE_FIELD_OPEN_TOKEN) && StringUtils.endsWith(fieldName, CommandField.NATIVE_FIELD_CLOSE_TOKEN)) {
//            isNative = true;
//            this.fieldName = StringUtils.substring(fieldName, CommandField.NATIVE_FIELD_OPEN_TOKEN.length(), fieldName.length() - CommandField.NATIVE_FIELD_CLOSE_TOKEN.length());
//        }
//        if (analyseTableAlias && StringUtils.indexOf(this.fieldName, ".") != -1) {
//            String[] fieldInfo = StringUtils.split(this.fieldName, ".");
//            this.tableAlias = fieldInfo[0];
//            this.fieldName = fieldInfo[1];
//        }
//    }
//
//    /**
//     * fieldOperator是否需要括号
//     *
//     * @return boolean
//     */
//    public boolean isFieldOperatorNeedBracket() {
//        return StringUtils.indexOf(StringUtils.upperCase(this.getFieldOperator()), "IN") != -1;
//    }
//
//
//    /**
//     * 字段来源
//     */
//    public enum Type {
//
//        /**
//         * Entity处理的，会有class
//         */
//        ENTITY_FIELD,
//
//        /**
//         * 手动添加组装的
//         */
//        MANUAL_FIELD,
//
//        /**
//         * Where append type.
//         */
//        WHERE_APPEND;
//
//        /**
//         * field名字是否需要解析表别名
//         *
//         * @param type the type
//         * @return boolean
//         */
//        public static boolean isAnalyseTableAlias(Type type) {
//            //append片断无需解析表别名
//            return type != WHERE_APPEND;
//        }
//    }
//
//    public String getFieldName() {
//        return fieldName;
//    }
//
//    public void setFieldName(String fieldName) {
//        this.fieldName = fieldName;
//    }
//
//    public String getTableAlias() {
//        return tableAlias;
//    }
//
//    public void setTableAlias(String tableAlias) {
//        this.tableAlias = tableAlias;
//    }
//
//    public String getLogicalOperator() {
//        return logicalOperator;
//    }
//
//    public void setLogicalOperator(String logicalOperator) {
//        this.logicalOperator = logicalOperator;
//    }
//
//    public String getFieldOperator() {
//        return fieldOperator;
//    }
//
//    public void setFieldOperator(String fieldOperator) {
//        this.fieldOperator = fieldOperator;
//    }
//
//    public Object getValue() {
//        return value;
//    }
//
//    public void setValue(Object value) {
//        this.value = value;
//    }
//
//    public boolean isNative() {
//        return isNative;
//    }
//
//    public void setNative(boolean aNative) {
//        isNative = aNative;
//    }
//
//    public Type getType() {
//        return type;
//    }
//
//    public void setType(Type type) {
//        this.type = type;
//    }
//
//    public Class<?> getCls() {
//        return cls;
//    }
//
//    public void setCls(Class<?> cls) {
//        this.cls = cls;
//    }
//}
