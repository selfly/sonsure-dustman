//package com.sonsure.dumper.core.command.simple;
//
//import com.sonsure.dumper.core.command.AbstractCommandDetailsBuilder;
//import com.sonsure.dumper.core.command.CommandParameters;
//import lombok.Getter;
//
//import java.util.Map;
//
///**
// * @author selfly
// */
//@Getter
//public abstract class AbstractSimpleCommandDetailsBuilder<T extends SimpleCommandDetailsBuilder<T>> extends AbstractCommandDetailsBuilder<T> implements SimpleCommandDetailsBuilder<T> {
//
//    protected String command;
//    protected CommandParameters commandParameters;
//
//    public AbstractSimpleCommandDetailsBuilder() {
//        this.commandParameters = new CommandParameters();
//    }
//
//    @Override
//    public T command(String command) {
//        this.command = command;
//        return this.getSelf();
//    }
//
//    @Override
//    public T parameter(String name, Object value) {
//        this.commandParameters.addParameter(name, value);
//        return getSelf();
//    }
//
//    @Override
//    public T parameters(Map<String, Object> parameters) {
//        if (parameters == null) {
//            return getSelf();
//        }
//        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
//            this.getCommandParameters().addParameter(entry.getKey(), entry.getValue());
//        }
//        return getSelf();
//    }
//
//    //    public void command(String command) {
////        this.simpleContext.setCommand(command);
////    }
////
////    public void parameter(String name, Object value) {
////        this.simpleContext.addCommandParameter(name, value);
////    }
////
////    public void parameters(Map<String, Object> parameters) {
////        this.simpleContext.addCommandParameters(parameters);
////    }
////
////    public Context getSimpleContext() {
////        return simpleContext;
////    }
//
////    public static class Context extends QueryCommandContextBuilderContext {
////
////        private String command;
////
////        private final List<ParameterObject> commandParameters;
////
////        public Context() {
////            this.commandParameters = new ArrayList<>();
////        }
////
////        public void addCommandParameter(String name, Object value) {
////            commandParameters.add(new ParameterObject(name, value));
////        }
////
////        public void addCommandParameters(Map<String, Object> parameters) {
////            if (parameters == null) {
////                return;
////            }
////            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
////                this.addCommandParameter(entry.getKey(), entry.getValue());
////            }
////        }
////
////        public Object getParameters() {
////            if (isNamedParameter()) {
////                return getParameterObjects().stream()
////                        .collect(Collectors.toMap(ParameterObject::getName, ParameterObject::getValue, (v1, v2) -> v1));
////            } else {
////                return getParameterObjects().stream()
////                        .map(ParameterObject::getValue)
////                        .collect(Collectors.toList());
////            }
////        }
////
////        public String getCommand() {
////            return command;
////        }
////
////        public void setCommand(String command) {
////            this.command = command;
////        }
////
////        public List<ParameterObject> getParameterObjects() {
////            return commandParameters;
////        }
////    }
//}
