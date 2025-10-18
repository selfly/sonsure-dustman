package com.sonsure.dumper.core.command.build;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author selfly
 */
public class CacheEntityClassWrapper {

    private static final Map<Class<?>, EntityClassWrapper> CACHE = new WeakHashMap<>();

    private final EntityClassWrapper entityClassWrapper;

    public CacheEntityClassWrapper(Class<?> entityClass) {
        this.entityClassWrapper = CACHE.computeIfAbsent(entityClass, k -> new EntityClassWrapper(entityClass));
    }

    public Class<?> getEntityClass() {
        return this.entityClassWrapper.getEntityClass();
    }

    public Object getClassAnnotation() {
        return this.entityClassWrapper.getClassAnnotation();
    }

    public Collection<EntityClassFieldWrapper> getEntityFields() {
        return this.entityClassWrapper.getEntityFields();
    }

    public EntityClassFieldWrapper getEntityField(String fileName) {
        return this.entityClassWrapper.getEntityField(fileName);
    }

    public EntityClassFieldWrapper getMappedField(String columnName) {
        return this.entityClassWrapper.getMappedField(columnName.toLowerCase());
    }

    public EntityClassFieldWrapper getPrimaryKeyField() {
        return this.entityClassWrapper.getPrimaryKeyField();
    }

}
