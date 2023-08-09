package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.management.CommandClass;
import com.sonsure.dumper.core.management.CommandField;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Command context builder context.
 *
 * @author liyd
 */
public class CommandContextBuilderContext {

    private boolean forceNative;

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
     * Create command class .
     *
     * @param cls       the cls
     * @param aliasName the alias name
     * @return the command class
     */
    protected CommandClass createCommandClass(Class<?> cls, String aliasName) {
        return new CommandClass(cls, aliasName);
    }

    /**
     * Create class field .
     *
     * @param name              the name
     * @param analyseTableAlias the analyse table alias
     * @param type              the type
     * @return the class field
     */
    protected CommandField createCommandClassField(String name, boolean analyseTableAlias, CommandField.Type type) {
        return this.createCommandClassField(name, analyseTableAlias, type, null);
    }

    /**
     * Create class field .
     *
     * @param name              the name
     * @param analyseTableAlias analyse table alias
     * @return the class field
     */
    protected CommandField createCommandClassField(String name, boolean analyseTableAlias, CommandField.Type type, Class<?> cls) {
        return new CommandField(name, analyseTableAlias, type, cls);
    }

    public boolean isForceNative() {
        return forceNative;
    }

    public void setForceNative(boolean forceNative) {
        this.forceNative = forceNative;
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
