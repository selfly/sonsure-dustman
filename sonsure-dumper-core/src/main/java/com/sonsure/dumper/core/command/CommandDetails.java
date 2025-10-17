///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   http://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.core.command;
//
//import com.sonsure.dumper.common.model.Pagination;
//import lombok.Getter;
//import lombok.Setter;
//
///**
// * 执行的命令内容
// * <p>
// *
// * @author liyd
// * @since 17 /4/12
// */
//@Getter
//@Setter
//public class CommandDetails {
//
//    private ExecutionType executionType;
//
//    private ToggleCase toggleCase;
//
//    /**
//     * 命令，一般指sql
//     */
//    private String command;
//
//    private CommandParameters commandParameters;
//
//    /**
//     * The Is native command.
//     */
//    private boolean forceNative;
//
//    /**
//     * The Is named parameter.
//     */
//    private boolean isNamedParameter;
//
//    /**
//     * 返回值类型，如果是native操作又不指定，可能为null
//     */
//    private Class<?> resultType;
//
//    /**
//     * 主键值，pkValueByDb=false才有
//     */
//    private GenerateKey generateKey;
//
//    private Pagination pagination;
//
//    private boolean disableCountQuery;
//
//    public CommandDetails() {
//        forceNative = false;
//        isNamedParameter = false;
//    }
//
//}
