package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Command context builder context.
 *
 * @author liyd
 */
@Getter
@Setter
public class CommandContextBuilderContext {

    private boolean nativeCommand;

    private boolean namedParameter;

    private final Set<ModelClassDetails> modelMetadata;

    /**
     * where group orderBy 等context为true
     */
    private boolean subBuilderContext;

    public CommandContextBuilderContext() {
        this.modelMetadata = new HashSet<>(4);
        this.subBuilderContext = false;
    }

    public void addModelClass(ModelClassDetails modelClassDetails) {
        this.modelMetadata.add(modelClassDetails);
    }

    /**
     * Gets unique model class.
     *
     * @return the unique model class
     */
    public ModelClassDetails getUniqueModelClass() {
        final Set<ModelClassDetails> mcs = this.getModelMetadata();
        if (mcs == null || mcs.size() != 1) {
            throw new SonsureJdbcException("当前执行业务不止一个Model Class");
        }
        return mcs.iterator().next();
    }

}
