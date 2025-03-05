//package com.sonsure.dumper.core.command.entity;
//
//import com.sonsure.dumper.core.command.AbstractCommonCommandDetailsBuilder;
//import com.sonsure.dumper.core.command.CommandContextBuilderContext;
//import com.sonsure.dumper.core.command.CommandDetails;
//import com.sonsure.dumper.core.command.ParameterObject;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//import com.sonsure.dumper.core.exception.SonsureJdbcException;
//import com.sonsure.dumper.core.management.CommandField;
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author liyd
// */
//public class ConditionCommandBuilderImpl extends AbstractCommonCommandDetailsBuilder {
//
//    private final Context conditionContext;
//
//    public ConditionCommandBuilderImpl(Context conditionContext) {
//        super(conditionContext);
//        this.conditionContext = conditionContext;
//        this.conditionContext.setSubBuilderContext(true);
//    }
//
//    public void addWhereField(String logicalOperator, String name, String fieldOperator, Object value, CommandField.Type type) {
//        boolean analyseTableAlias = CommandField.Type.isAnalyseTableAlias(type);
//        CommandField commandField = this.createCommandClassField(name, analyseTableAlias, CommandField.Type.MANUAL_FIELD);
//        commandField.setLogicalOperator(logicalOperator);
//        commandField.setFieldOperator(fieldOperator);
//        commandField.setValue(value);
//        commandField.setType(type);
//        this.conditionContext.addWhereField(commandField);
//    }
//
//    public void addWhereFields(List<CommandField> commandFields) {
//        this.conditionContext.addWhereFields(commandFields);
//    }
//
//    public void setLastWhereFieldSize(int size) {
//        this.conditionContext.setLastFieldSize(size);
//    }
//
//    public void removeLastWhereFields() {
//        this.conditionContext.removeLastWhereFields();
//    }
//
//    public void setIf(boolean iff) {
//        this.conditionContext.setIfCondition(iff);
//    }
//
//    public boolean isIf() {
//        return this.conditionContext.isIfCondition();
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
//        final boolean isNamedParameter = this.conditionContext.isNamedParameter();
//        List<CommandField> whereFields = this.conditionContext.getWhereFields();
//        if (whereFields.isEmpty()) {
//            return null;
//        }
//
//        StringBuilder whereCommand = new StringBuilder(" ");
//        List<ParameterObject> commandParameters = new ArrayList<>();
//        for (CommandField commandField : whereFields) {
//
//            //在前面处理，有单独where or and 的情况
//            if (StringUtils.isNotBlank(commandField.getLogicalOperator())) {
//                //没有where不管如何and or等操作符都换成where
//                if (whereCommand.length() < 5) {
//                    whereCommand.append("where ");
//                } else {
//                    whereCommand.append(commandField.getLogicalOperator()).append(" ");
//                }
//            }
//            //只有where or and 的情况
//            if (StringUtils.isBlank(commandField.getFieldName())) {
//                continue;
//            }
//            final String filedCommandName = this.getFiledCommandName(commandField, jdbcEngineConfig);
//            if (commandField.getType() == CommandField.Type.WHERE_APPEND) {
//                whereCommand.append(filedCommandName);
//                if (commandField.getValue() != null) {
//                    if (commandField.getValue() instanceof Object[]) {
//                        Object[] values = (Object[]) commandField.getValue();
//                        for (int i = 0; i < values.length; i++) {
//                            commandParameters.add(new ParameterObject(commandField.getFieldName() + i, values[i]));
//                        }
//                    } else if (commandField.getValue() instanceof Map) {
//                        final Map<String, Object> valueMap = (Map<String, Object>) commandField.getValue();
//                        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
//                            commandParameters.add(new ParameterObject(entry.getKey(), entry.getValue()));
//                        }
//                    } else {
//                        throw new SonsureJdbcException("不支持的参数类型");
//                    }
//                }
//            } else if (commandField.getValue() == null) {
//                String operator = StringUtils.isBlank(commandField.getFieldOperator()) ? "is" : commandField.getFieldOperator();
//                whereCommand.append(this.getTableAliasField(commandField.getTableAlias(), filedCommandName))
//                        .append(" ")
//                        .append(operator)
//                        .append(" null ");
//            } else if (commandField.getValue() instanceof Object[]) {
//                this.processArrayArgs(commandField, whereCommand, commandParameters, jdbcEngineConfig);
//            } else {
//                whereCommand.append(this.getTableAliasField(commandField.getTableAlias(), filedCommandName))
//                        .append(" ")
//                        .append(commandField.getFieldOperator())
//                        .append(" ");
//
//                //native 不传参方式
//                if (commandField.isNative()) {
//                    whereCommand.append(commandField.isFieldOperatorNeedBracket() ? String.format(" ( %s ) ", commandField.getValue()) : String.format(" %s ", commandField.getValue()));
//                } else {
//                    final String placeholder = this.createParameterPlaceholder(commandField.getFieldName(), isNamedParameter);
//                    whereCommand.append(commandField.isFieldOperatorNeedBracket() ? String.format(" ( %s ) ", placeholder) : String.format(" %s ", placeholder));
//                    commandParameters.add(new ParameterObject(commandField.getFieldName(), commandField.getValue()));
//                }
//            }
//        }
//        //只有where的情况
//        if (whereCommand.length() < 8) {
//            whereCommand.delete(0, whereCommand.length());
//        }
//        CommandDetails commandDetails = new CommandDetails();
//        commandDetails.setCommand(whereCommand.toString());
//        commandDetails.addCommandParameters(commandParameters);
//        return commandDetails;
//
//    }
//
//
//    /**
//     * 处理数组参数
//     */
//    protected void processArrayArgs(CommandField commandField, StringBuilder whereCommand, List<ParameterObject> parameters, JdbcEngineConfig jdbcEngineConfig) {
//        final String filedCommandName = this.getFiledCommandName(commandField, jdbcEngineConfig);
//        String aliasField = this.getTableAliasField(commandField.getTableAlias(), filedCommandName);
//        Object[] args = (Object[]) commandField.getValue();
//        if (commandField.isFieldOperatorNeedBracket()) {
//            whereCommand.append(aliasField).append(" ").append(commandField.getFieldOperator()).append(" (");
//            for (int i = 0; i < args.length; i++) {
//                if (commandField.isNative()) {
//                    whereCommand.append(args[i]);
//                } else {
//                    final String name = commandField.getFieldName() + i;
//                    String placeholder = this.createParameterPlaceholder(name, this.conditionContext.isNamedParameter());
//                    whereCommand.append(placeholder);
//                    parameters.add(new ParameterObject(name, args[i]));
//                }
//                if (i != args.length - 1) {
//                    whereCommand.append(",");
//                }
//            }
//            whereCommand.append(") ");
//        } else {
//            if (ArrayUtils.getLength(args) > 1) {
//                whereCommand.append(" (");
//            }
//            for (int i = 0; i < args.length; i++) {
//                whereCommand.append(aliasField).append(" ").append(commandField.getFieldOperator());
//                if (commandField.isNative()) {
//                    whereCommand.append(String.format(" %s ", args[i]));
//                } else {
//                    final String name = commandField.getFieldName() + i;
//                    String placeholder = this.createParameterPlaceholder(name, this.conditionContext.isNamedParameter());
//                    whereCommand.append(" ").append(placeholder).append(" ");
//                    parameters.add(new ParameterObject(name, args[i]));
//                }
//                if (i != args.length - 1) {
//                    whereCommand.append(" or ");
//                }
//            }
//            if (ArrayUtils.getLength(args) > 1) {
//                whereCommand.append(") ");
//            }
//        }
//    }
//
//    public static class Context extends CommandContextBuilderContext {
//
//        @Getter
//        private final List<CommandField> whereFields;
//
//        @Setter
//        @Getter
//        private boolean ifCondition = true;
//
//        @Setter
//        private int lastFieldSize = 1;
//
//        public Context() {
//            this.whereFields = new ArrayList<>();
//        }
//
//        public void addWhereField(CommandField commandField) {
//            this.getWhereFields().add(commandField);
//        }
//
//        public void addWhereFields(List<CommandField> commandFields) {
//            this.getWhereFields().addAll(commandFields);
//        }
//
//        public void removeLastWhereFields() {
//            for (int i = this.lastFieldSize; i > 0; i--) {
//                this.getWhereFields().remove(this.getWhereFields().size() - 1);
//            }
//        }
//
//    }
//
//}
