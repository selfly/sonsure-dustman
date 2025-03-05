package com.sonsure.dumper.core.command;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author selfly
 */
@Getter
@Setter
public class CommandParameters {

    private final List<ParameterObject> parameterObjects;

    private List<Object> parsedParameterValues;
    private List<String> parsedParameterNames;

    public CommandParameters() {
        this.parameterObjects = new ArrayList<>(16);
    }

    public void addParameter(String name, Object value) {
        this.parameterObjects.add(new ParameterObject(name, value));
    }

    public Map<String, Object> getParameterMap() {
        return this.parameterObjects.stream()
                .collect(Collectors.toMap(ParameterObject::getName, ParameterObject::getValue));
    }

    public List<Object> getParameterValues() {
        return this.getParameterObjects().stream()
                .map(ParameterObject::getValue)
                .collect(Collectors.toList());
    }

}
