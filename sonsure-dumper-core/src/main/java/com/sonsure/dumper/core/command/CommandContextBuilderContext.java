package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Command context builder context.
 *
 * @author liyd
 */
public class CommandContextBuilderContext {

    private boolean nativeCommand;

    private boolean namedParameter;

    private final Set<Class<?>> modelClasses;

    /**
     * where group orderBy 等context为true
     */
    private boolean subBuilderContext;

    public CommandContextBuilderContext() {
        this.modelClasses = new HashSet<>();
        this.subBuilderContext = false;
    }

    public void addModelClass(Class<?> cls) {
        this.modelClasses.add(cls);
    }

    /**
     * Gets unique model class.
     *
     * @return the unique model class
     */
    public Class<?> getUniqueModelClass() {
        final Set<Class<?>> mcs = this.getModelClasses();
        if (mcs == null || mcs.size() != 1) {
            throw new SonsureJdbcException("当前执行业务不止一个Model Class");
        }
        return mcs.iterator().next();
    }

    public boolean isNativeCommand() {
        return nativeCommand;
    }

    public void setNativeCommand(boolean nativeCommand) {
        this.nativeCommand = nativeCommand;
    }

    public boolean isNamedParameter() {
        return namedParameter;
    }

    public void setNamedParameter(boolean namedParameter) {
        this.namedParameter = namedParameter;
    }

    public Set<Class<?>> getModelClasses() {
        return modelClasses;
    }

    public boolean isSubBuilderContext() {
        return subBuilderContext;
    }

    public void setSubBuilderContext(boolean subBuilderContext) {
        this.subBuilderContext = subBuilderContext;
    }
}
