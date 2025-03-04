package com.sonsure.dumper.core.command.simple;

import com.sonsure.dumper.core.command.CommandDetailsBuilder;

import java.util.Map;

/**
 * @author selfly
 */
public interface SimpleCommandDetailsBuilder<T extends SimpleCommandDetailsBuilder<T>> extends CommandDetailsBuilder<T> {

    /**
     * Command t.
     *
     * @param command the command
     * @return the t
     */
    T command(String command);

    /**
     * Parameter t.
     *
     * @param name  the name
     * @param value the value
     * @return the t
     */
    T parameter(String name, Object value);

    /**
     * Parameters t.
     *
     * @param parameters the parameters
     * @return the t
     */
    T parameters(Map<String, Object> parameters);

    /**
     * Named parameter
     *
     * @return the t
     */
    T namedParameter();

}
