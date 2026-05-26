package com.sonsure.dustman.jdbc.config;

import com.sonsure.dustman.jdbc.command.batch.BatchUpdateExecutor;
import com.sonsure.dustman.jdbc.command.batch.BatchUpdateExecutorImpl;
import com.sonsure.dustman.jdbc.command.entity.*;
import com.sonsure.dustman.jdbc.command.mybatis.MybatisExecutor;
import com.sonsure.dustman.jdbc.command.mybatis.MybatisExecutorImpl;
import com.sonsure.dustman.jdbc.command.natives.NativeExecutor;
import com.sonsure.dustman.jdbc.command.natives.NativeExecutorImpl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author selfly
 */
public class InternalCommandExecutorCreatorImpl implements CommandExecutorCreator {

    private final Map<Class<?>, BiFunction<JdbcContext, Object[], ?>> creators;

    public InternalCommandExecutorCreatorImpl() {
        this.creators = new LinkedHashMap<>();
        this.creators.put(Insert.class, (ctx, params) -> new InsertImpl(ctx));
        this.creators.put(Select.class, (ctx, params) -> new SelectImpl<>(ctx, params));
        this.creators.put(Update.class, (ctx, params) -> new UpdateImpl(ctx));
        this.creators.put(Delete.class, (ctx, params) -> new DeleteImpl(ctx));
        this.creators.put(NativeExecutor.class, (ctx, params) -> new NativeExecutorImpl(ctx));
        this.creators.put(MybatisExecutor.class, (ctx, params) -> new MybatisExecutorImpl(ctx));
        this.creators.put(BatchUpdateExecutor.class, (ctx, params) -> new BatchUpdateExecutorImpl(ctx));
    }

    @Override
    public Class<?>[] getCommandExecutorClasses() {
        return creators.keySet().toArray(new Class<?>[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Class<T> commandExecutorClass, JdbcContext jdbcContext, Object... params) {
        BiFunction<JdbcContext, Object[], ?> creator = creators.get(commandExecutorClass);
        if (creator != null) {
            return (T) creator.apply(jdbcContext, params);
        }
        return null;
    }
}
