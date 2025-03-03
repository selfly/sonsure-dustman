package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.mapping.MappingHandler;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author selfly
 */
@AllArgsConstructor
public class EntityClassDetailsImpl implements ModelClassDetails{

    private final Class<?> entityClass;
    private final MappingHandler mappingHandler;

    @Override
    public String getModelName() {
        return "";
    }

    @Override
    public ModelFieldDetails getPrimaryKeyFiled() {
        return null;
    }

    @Override
    public List<ModelFieldDetails> getModelFields() {
        return Collections.emptyList();
    }
}
