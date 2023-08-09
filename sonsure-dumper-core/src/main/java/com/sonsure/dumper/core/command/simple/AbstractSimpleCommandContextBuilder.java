package com.sonsure.dumper.core.command.simple;

import com.sonsure.dumper.core.command.CommandParameter;
import com.sonsure.dumper.core.command.QueryCommandContextBuilder;
import com.sonsure.dumper.core.command.QueryCommandContextBuilderContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author selfly
 */
public abstract class AbstractSimpleCommandContextBuilder extends QueryCommandContextBuilder {

    protected final Context simpleContext;

    public AbstractSimpleCommandContextBuilder(Context simpleContext) {
        super(simpleContext);
        this.simpleContext = simpleContext;
    }

    public void command(String command) {
        this.simpleContext.setCommand(command);
    }

    public void parameter(String name, Object value) {
        this.simpleContext.addCommandParameter(name, value);
    }

    public void parameters(Map<String, Object> parameters) {
        this.simpleContext.addCommandParameters(parameters);
    }

    public Context getSimpleContext() {
        return simpleContext;
    }

    public static class Context extends QueryCommandContextBuilderContext {

        private String command;

        private final List<CommandParameter> commandParameters;

        public Context() {
            this.commandParameters = new ArrayList<>();
        }

        public void addCommandParameter(String name, Object value) {
            commandParameters.add(new CommandParameter(name, value));
        }

        public void addCommandParameters(Map<String, Object> parameters) {
            if (parameters == null) {
                return;
            }
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                this.addCommandParameter(entry.getKey(), entry.getValue());
            }
        }

        public Object getParameters() {
            if (isNamedParameter()) {
                return getCommandParameters().stream()
                        .collect(Collectors.toMap(CommandParameter::getName, CommandParameter::getValue, (v1, v2) -> v1));
            } else {
                return getCommandParameters().stream()
                        .map(CommandParameter::getValue)
                        .collect(Collectors.toList());
            }
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public List<CommandParameter> getCommandParameters() {
            return commandParameters;
        }
    }
}
