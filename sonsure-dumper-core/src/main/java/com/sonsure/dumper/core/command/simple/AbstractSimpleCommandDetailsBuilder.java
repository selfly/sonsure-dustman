package com.sonsure.dumper.core.command.simple;

import com.sonsure.dumper.core.command.AbstractCommandDetailsBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import lombok.Getter;

import java.util.Map;

/**
 * @author selfly
 */
@Getter
public abstract class AbstractSimpleCommandDetailsBuilder<T extends SimpleCommandDetailsBuilder<T>> extends AbstractCommandDetailsBuilder<T> implements SimpleCommandDetailsBuilder<T> {

    protected String command;
    protected boolean namedParameter = false;

    public AbstractSimpleCommandDetailsBuilder(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public T command(String command) {
        this.command = command;
        return this.getSelf();
    }

    @Override
    public T parameter(String name, Object value) {
        return null;
    }

    @Override
    public T parameters(Map<String, Object> parameters) {
        return null;
    }

    @Override
    public T namedParameter() {
        this.namedParameter = true;
        return this.getSelf();
    }


    //    public void command(String command) {
//        this.simpleContext.setCommand(command);
//    }
//
//    public void parameter(String name, Object value) {
//        this.simpleContext.addCommandParameter(name, value);
//    }
//
//    public void parameters(Map<String, Object> parameters) {
//        this.simpleContext.addCommandParameters(parameters);
//    }
//
//    public Context getSimpleContext() {
//        return simpleContext;
//    }

//    public static class Context extends QueryCommandContextBuilderContext {
//
//        private String command;
//
//        private final List<CommandParameter> commandParameters;
//
//        public Context() {
//            this.commandParameters = new ArrayList<>();
//        }
//
//        public void addCommandParameter(String name, Object value) {
//            commandParameters.add(new CommandParameter(name, value));
//        }
//
//        public void addCommandParameters(Map<String, Object> parameters) {
//            if (parameters == null) {
//                return;
//            }
//            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
//                this.addCommandParameter(entry.getKey(), entry.getValue());
//            }
//        }
//
//        public Object getParameters() {
//            if (isNamedParameter()) {
//                return getCommandParameters().stream()
//                        .collect(Collectors.toMap(CommandParameter::getName, CommandParameter::getValue, (v1, v2) -> v1));
//            } else {
//                return getCommandParameters().stream()
//                        .map(CommandParameter::getValue)
//                        .collect(Collectors.toList());
//            }
//        }
//
//        public String getCommand() {
//            return command;
//        }
//
//        public void setCommand(String command) {
//            this.command = command;
//        }
//
//        public List<CommandParameter> getCommandParameters() {
//            return commandParameters;
//        }
//    }
}
