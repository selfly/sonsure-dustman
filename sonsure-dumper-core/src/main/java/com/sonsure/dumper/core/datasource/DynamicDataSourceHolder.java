///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   http://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.core.datasource;
//
///**
// * 动态数据源切换标识
// * <p/>
// * Created by liyd on 2015-11-2.
// */
//public class DynamicDataSourceHolder {
//
//    private static final ThreadLocal<DataSourceContext> DATASOURCE_CONTEXT_LOCAL = new ThreadLocal<DataSourceContext>();
//
//    /**
//     * 设置数据源模式为写
//     */
//    public static void setWriteMode() {
//        DataSourceContext dataSourceContext = DATASOURCE_CONTEXT_LOCAL.get();
//        //已经持有且可写，直接返回
//        if (dataSourceContext != null && dataSourceContext.isWrite()) {
//            return;
//        }
//
//        if (dataSourceContext == null) {
//            dataSourceContext = new DataSourceContext();
//            dataSourceContext.setWrite(true);
//            DATASOURCE_CONTEXT_LOCAL.set(dataSourceContext);
//        }
//    }
//
//    /**
//     * 设置数据源模式为读
//     */
//    public static void setReadMode() {
//        DataSourceContext dataSourceContext = DATASOURCE_CONTEXT_LOCAL.get();
//        //已经持有，直接返回
//        if (dataSourceContext != null) {
//            return;
//        }
//
//        dataSourceContext = new DataSourceContext();
//        dataSourceContext.setWrite(false);
//        DATASOURCE_CONTEXT_LOCAL.set(dataSourceContext);
//    }
//
//    /**
//     * 是否写模式
//     *
//     * @return
//     */
//    public static boolean isWriteMode() {
//        DataSourceContext dataSourceContext = DATASOURCE_CONTEXT_LOCAL.get();
//        return dataSourceContext == null ? true : dataSourceContext.isWrite();
//    }
//
//    /**
//     * 是否读模式
//     *
//     * @return
//     */
//    public static boolean isReadMode() {
//        return !isWriteMode();
//    }
//
//    /**
//     * 获取DataSource key
//     *
//     * @return
//     */
//    public static String getDataSourceKey() {
//        DataSourceContext dataSourceContext = DATASOURCE_CONTEXT_LOCAL.get();
//        return dataSourceContext == null ? "" : dataSourceContext.getKey();
//    }
//
//
//    public static void setDataSourceKey(String key) {
//        //不可能为null
//        DATASOURCE_CONTEXT_LOCAL.get().setKey(key);
//    }
//
//    /**
//     * 清除
//     */
//    public static void clear() {
//        DATASOURCE_CONTEXT_LOCAL.remove();
//    }
//
//
//    static class DataSourceContext {
//
//        /**
//         * dataSource key
//         */
//        private String key;
//
//        /**
//         * 是否可写
//         */
//        private boolean isWrite;
//
//        public String getKey() {
//            return key;
//        }
//
//        public void setKey(String key) {
//            this.key = key;
//        }
//
//        public boolean isWrite() {
//            return isWrite;
//        }
//
//        public void setWrite(boolean write) {
//            isWrite = write;
//        }
//    }
//}
