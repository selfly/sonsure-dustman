package com.sonsure.dumper.core.config;

/**
 * @author selfly
 */
public interface CommandExecutorCreator {

    /**
     * Gets command executor class.
     *
     * @return the command executor class
     */
    Class<?>[] getCommandExecutorClasses();

    /**
     * Create t.
     *
     * @param <T>                  the type parameter
     * @param commandExecutorClass the command executor class
     * @param jdbcContext          the jdbc context
     * @param params               the params
     * @return the t
     */
    <T> T create(Class<T> commandExecutorClass, JdbcContext jdbcContext, Object... params);
}
