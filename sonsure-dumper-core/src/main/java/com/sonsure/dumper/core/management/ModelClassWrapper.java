package com.sonsure.dumper.core.management;

import lombok.Getter;

import java.util.Collection;

/**
 * @author selfly
 */
@Getter
public class ModelClassWrapper {

    private final Class<?> modelClass;

    public ModelClassWrapper(Class<?> modelClass) {
        this.modelClass = modelClass;
    }

    public String getModelName() {
        return this.getModelClass().getSimpleName();
    }

    public ModelClassDetails getModelClassDetails() {
        return ModelClassDetailsHelper.getClassDetails(this.getModelClass());
    }
    
    public Collection<ModelClassFieldDetails> getModelFields() {
        ModelClassDetails classDetails = this.getModelClassDetails();
        return classDetails.getModelFields();
    }

    public ModelClassFieldDetails getPrimaryKeyField() {
        return getModelClassDetails().getPrimaryKeyField();
    }
}
