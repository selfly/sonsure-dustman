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
    public <T> T create(Class<T> commandExecutorClass, JdbcContext jdbcContext, Object... params) {
        if (Insert.class == commandExecutorClass) {
            return (T) new InsertImpl(jdbcContext);
        }
        if (Select.class == commandExecutorClass) {
            return (T) new SelectImpl<>(jdbcContext, params);
        }
        if (Update.class == commandExecutorClass) {
            return (T) new UpdateImpl(jdbcContext);
        }
        if (Delete.class == commandExecutorClass) {
            return (T) new DeleteImpl(jdbcContext);
        }
        if (NativeExecutor.class == commandExecutorClass) {
            return (T) new NativeExecutorImpl(jdbcContext);
        }
        if (MybatisExecutor.class == commandExecutorClass) {
            return (T) new MybatisExecutorImpl(jdbcContext);
        }
        if (BatchUpdateExecutor.class == commandExecutorClass) {
            return (T) new BatchUpdateExecutorImpl(jdbcContext);
        }
        return null;
    }
}
