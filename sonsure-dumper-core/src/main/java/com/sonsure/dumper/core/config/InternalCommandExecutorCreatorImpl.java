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
    public <T> T create(Class<T> commandExecutorClass, JdbcExecutorConfig jdbcExecutorConfig, Object... params) {
        if (Insert.class == commandExecutorClass) {
            return (T) new InsertImpl(jdbcExecutorConfig);
        }
        if (Select.class == commandExecutorClass) {
            return (T) new SelectImpl<>(jdbcExecutorConfig, params);
        }
        if (Update.class == commandExecutorClass) {
            return (T) new UpdateImpl(jdbcExecutorConfig);
        }
        if (Delete.class == commandExecutorClass) {
            return (T) new DeleteImpl(jdbcExecutorConfig);
        }
        if (NativeExecutor.class == commandExecutorClass) {
            return (T) new NativeExecutorImpl(jdbcExecutorConfig);
        }
        if (MybatisExecutor.class == commandExecutorClass) {
            return (T) new MybatisExecutorImpl(jdbcExecutorConfig);
        }
        if (BatchUpdateExecutor.class == commandExecutorClass) {
            return (T) new BatchUpdateExecutorImpl(jdbcExecutorConfig);
        }
        return null;
    }
}
