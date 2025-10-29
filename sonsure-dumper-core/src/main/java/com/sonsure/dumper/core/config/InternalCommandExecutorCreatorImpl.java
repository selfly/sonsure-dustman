package com.sonsure.dumper.core.config;

import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutorImpl;
import com.sonsure.dumper.core.command.entity.*;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutorImpl;
import com.sonsure.dumper.core.command.natives.NativeExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutorImpl;

/**
 * @author selfly
 */
public class InternalCommandExecutorCreatorImpl implements CommandExecutorCreator {

    @Override
    public Class<?>[] getCommandExecutorClasses() {
        return new Class<?>[]{Insert.class, Select.class, Update.class, Delete.class, NativeExecutor.class, MybatisExecutor.class, BatchUpdateExecutor.class};
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig, Object... params) {
        if (Insert.class == commandExecutorClass) {
            return (T) new InsertImpl(jdbcEngineConfig);
        }
        if (Select.class == commandExecutorClass) {
            return (T) new SelectImpl<>(jdbcEngineConfig, params);
        }
        if (Update.class == commandExecutorClass) {
            return (T) new UpdateImpl(jdbcEngineConfig);
        }
        if (Delete.class == commandExecutorClass) {
            return (T) new DeleteImpl(jdbcEngineConfig);
        }
        if (NativeExecutor.class == commandExecutorClass) {
            return (T) new NativeExecutorImpl(jdbcEngineConfig);
        }
        if (MybatisExecutor.class == commandExecutorClass) {
            return (T) new MybatisExecutorImpl(jdbcEngineConfig);
        }
        if (BatchUpdateExecutor.class == commandExecutorClass) {
            return (T) new BatchUpdateExecutorImpl(jdbcEngineConfig);
        }
        return null;
    }
}
