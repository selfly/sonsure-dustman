///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   http://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.core.command.entity;
//
//import com.sonsure.dumper.core.command.DynamicCommandDetailsBuilder;
//
///**
// * CommandContext构建
// * <p>
// *
// * @author liyd
// * @since  17 /4/11
// */
//public interface EntityCommandDetailsBuilder extends DynamicCommandDetailsBuilder<EntityCommandDetailsBuilder> {
//
//    /**
//     * From
//     *
//     * @param cls the cls
//     * @return the command details builder
//     */
//    EntityCommandDetailsBuilder from(Class<?> cls);
//
//    /**
//     * Add all columns entity command details builder.
//     *
//     * @return the entity command details builder
//     */
//    EntityCommandDetailsBuilder addAllColumns();
//
//    /**
//     * Insert into
//     *
//     * @param cls the cls
//     * @return the command details builder
//     */
//    EntityCommandDetailsBuilder insertInto(Class<?> cls);
//
//    /**
//     * Update command details builder.
//     *
//     * @param cls the cls
//     * @return the command details builder
//     */
//    EntityCommandDetailsBuilder update(Class<?> cls);
//
//    /**
//     * Delete from
//     *
//     * @param cls the cls
//     * @return the command details builder
//     */
//    EntityCommandDetailsBuilder deleteFrom(Class<?> cls);
//
//    /**
//     * Inner join entity command details builder.
//     *
//     * @param cls the cls
//     * @return the entity command details builder
//     */
//    EntityCommandDetailsBuilder innerJoin(Class<?> cls);
//
//    /**
//     * Sets field for object where pk.
//     *
//     * @param object the object
//     * @return the field for object where pk
//     */
//    EntityCommandDetailsBuilder setFieldForObjectWherePk(Object object);
//}
